(ns rocket-ship.ops.crate
 (:use (clojure.contrib strint core))
 (:require
   (pallet core resource)
   pallet.action.service
   (pallet.crate
     [tomcat :as tomcat]
     [etc-default :as default])))

(defn tomcat-deploy
  "Deploys the specified .war file to tomcat.  An optional :port kwarg
   defines the port that tomcat will serve on (defaults to 80)."
  [session warfile]
  (->
   session
   (pallet.action.service/with-restart "tomcat*"
     (tomcat/deploy "ROOT" :local-file warfile :clear-existing true))))

(defn tomcat-server
  [& {:keys [port] :or {port 80}}]
  (tomcat/server
   :shutdown "SHUTDOWN"
   (tomcat/service
    (tomcat/engine "catalina" "localhost")
    (tomcat/connector
     :port (str port) :protocol "HTTP/1.1"
     :connectionTimeout "20000"
     :redirectPort "8443"))))

(defn tomcat-config
  [session]
  (->
   session
   (tomcat/server-configuration)
   (default/write "tomcat6"
     ;; configure tomcat's heap to utilize 2/3 of machine's
     ;; total memory
     :JAVA_OPTS (try (->> session
                          :target-node
                          .getHardware
                          .getRam
                          (* 0.66)
                          int
                          (format "-Xmx%sm"))
                     (catch Exception e
                       "-Xmx512m"))
                                        ; allow tomcat to run on ports < 1024
     :AUTHBIND "yes")))
