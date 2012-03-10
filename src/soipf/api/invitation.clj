(ns soipf.api.invitation
  (:import (java.security MessageDigest))
  (:use somnium.congomongo
        soipf.db))

(defn sha1 [obj]
  (let [bytes (.getBytes (with-out-str (pr obj)))]
    (apply str
           (map #(Integer/toHexString (bit-and % 0xff))
                (.digest (MessageDigest/getInstance "SHA1") bytes)))))

(defn generate-invitation-code []
  (sha1 (str (java.util.Date.) (rand))))

(defn valid? [id]
  (not (get (retrieve id) :by)))

(defn create! [user]
  (insert! :invitations
           {:_id (new-id "invitations")
            :code (generate-invitation-code)
            :created-at (java.util.Date.)
            :from user}))

(defn retrieve [id]
  (fetch-one :invitations :where {:_id id}))

(defn use! [id new-user]
  (update! :invitations {:_id id}
           {:$set {:used-at (java.util.Date.)
                   :by new-user}}))