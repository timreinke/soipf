(ns soipf.db
  (:require [somnium.congomongo :as mongo]))

(defn new-id
  ([] (new-id "**global**"))
  ([key] (Integer/toString
          (:counter (mongo/fetch-and-modify :counters
                                            {:_id key}
                                            {:$inc {:counter 1}}
                                            :return-new? true
                                            :upsert? true))
          36)))
