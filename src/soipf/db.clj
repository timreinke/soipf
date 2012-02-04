(ns soipf.db
  (:require [somnium.congomongo :as mongo]))

(def mongo-connection (atom nil))

(defn get-mongo-connection []
  (if (nil? @mongo-connection)
    (swap! mongo-connection (fn [x] (mongo/make-connection "soipf")))
    @mongo-connection))

(defn new-id
  ([] (new-id "**global**"))
  ([key] (Integer/toString
          (:counter (mongo/fetch-and-modify :counters
                                            {:_id key}
                                            {:$inc {:counter 1}}
                                            :return-new? true
                                            :upsert? true))
          36)))
