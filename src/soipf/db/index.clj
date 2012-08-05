(ns soipf.db.index
  (:use soipf.db
        somnium.congomongo))

(defn add-indices []
  (with-mongo (get-mongo-connection)
    (add-index! :last-read [:user-id :thread-id])))