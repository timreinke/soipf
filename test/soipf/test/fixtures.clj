(ns soipf.test.fixtures
  (:use somnium.congomongo
        soipf.models.user)
  (:require [soipf.db :as db]))

(defn mongo-connection [f]
  (reset! db/mongo-connection (make-connection "test-db"))
  (with-mongo @db/mongo-connection
    (f)
    (drop-database! "test-db"))
  (reset! db/mongo-connection nil))

(defn mongo-cleanup-collections [& cs]
  (fn [f]
    (f)
    (map drop-coll! cs)))

(defn users
  ([f] (users f ["tim" "password" "password"]))
  ([f & logins]
     (dorun (map #(apply create-user! %) logins))
     (f)
     (drop-coll! :users)))

(defn invitations [f]
  (insert! :invitations {:_id "unused"
                         :used false})
  (insert! :invitations {:_id "used"
                         :used true})
  (f)
  (drop-coll! :invitations))

(defn reset-session [f]
  (reset! noir.session/mem {})
  (f))
