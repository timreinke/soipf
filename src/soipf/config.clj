(ns soipf.config
  (:require [somnium.congomongo :as mongo]))

(def mongo-connection (atom nil))

(defn get-mongo-connection []
  (if (nil? @mongo-connection)
    (swap! mongo-connection (fn [x] (mongo/make-connection "soipf")))
    @mongo-connection))
