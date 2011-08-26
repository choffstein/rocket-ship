(ns pallet.task.rocket-shutdown
 (:use (clojure.contrib strint core))
 (:require
   [servlet-test.ops.nodes :as webdeploy-nodes]
   pallet.core
   [org.jclouds.compute :as jcompute]
   [clojure.contrib.logging :as log]))

(defonce cloud-service (pallet.compute/compute-service-from-config-file "aws-ec2"))

(defn shutdown [prefix]
  (pallet.core/converge {(webdeploy-nodes/groupserver prefix) 0}
                        :compute cloud-service))

(when-let [[prefix] (and (= rocket-deploy (first *command-line-args*))
                         (rest *command-line-args*))]
  (let [prefix (.toLowerCase prefix)]
    (shutdown prefix))
  (shutdown-agents))
