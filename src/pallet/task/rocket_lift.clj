(ns pallet.task.rocket-lift
 (:use (clojure.contrib strint core))
 (:require
   [servlet-test.ops.nodes :as webdeploy-nodes]
   pallet.core
   pallet.utils
   [org.jclouds.compute :as jcompute]
   [clojure.contrib.logging :as log]))

(defonce cloud-service (pallet.compute/compute-service-from-config-file "aws-ec2"))

(when-not webdeploy-nodes/warfile-path
  (throw (IllegalStateException. "No .war file defined, cannot deploy")))

(defn lift [prefix]
  (pallet.core/lift (webdeploy-nodes/groupserver prefix)
                    :compute cloud-service
                    :phase [:deploy]))

(when-let [[prefix] (and (= rocket-deploy (first *command-line-args*))
                         (rest *command-line-args*))]
  (let [prefix (.toLowerCase prefix)]
    (lift prefix))
  (shutdown-agents))
