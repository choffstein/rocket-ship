(ns rocket-ship.ops.nodes
 (:use clojure.contrib.core)
 (:require
   [rocket-ship.ops.crate :as webdeploy-crate]
   (pallet core resource)
   (pallet.crate java
     [tomcat :as tomcat]
     [automated-admin-user :as admin])
   [clojure.contrib.logging :as log]))

(def warfile-path
  ; this is just making things easier for demonstration purposes
  ; for a real project, you'd likely want to parameterize this via
  ; a system property (or use the maven dependency plugin to copy
  ; a separately-build .war to a known location), so as to separate
  ; the production of the war artifact from its deployment
  (or (-?>> "target"
        java.io.File.
        .listFiles
        (filter #(.endsWith (.getName %) ".war"))
        first
        .getAbsolutePath)
    (when-not *compile-files*
      (log/fatal "No .war artifact available in target dir, deployment operations will FAIL"))))

(def nodespec (pallet.core/node-spec
               :image {:os-family :ubuntu
                      :os-description-matches "10.10"}
               :hardware {:min-ram 1024
                          ;;:hardware-id "t1.micro"
                          }
               ;; can optionally require cloud-specific images and node sizes if you like
               ;; (strongly recommended for real usage!)
               ;; :image-id "us-east-1/ami-508c7839"
               ;; :hardware-id "m1.small"
               :network {:inbound-ports [22 80]}))

(def tomcat-settings (tomcat/settings-map {:version 6}))

(def appserver
  (pallet.core/server-spec
   :phases {:bootstrap (pallet.phase/phase-fn
                        (admin/automated-admin-user))
            :settings (fn [session]
                        (tomcat/settings
                         session
                         (assoc tomcat-settings
                          :server (webdeploy-crate/tomcat-server))))
            :configure (pallet.phase/phase-fn
                        (pallet.crate.java/java :openjdk)
                        (tomcat/install)
                        (webdeploy-crate/tomcat-config))
            :deploy (pallet.phase/phase-fn
                     (webdeploy-crate/tomcat-deploy warfile-path))}))

(defn groupserver [group-prefix]
  (pallet.core/group-spec
   group-prefix
   :extends appserver
   :node-spec nodespec))
