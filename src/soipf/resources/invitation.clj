(ns soipf.models.invitation
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

(defn retrieve-invitation [id]
  (fetch-one :invitations :where {:_id id}))

(defn create-invitation! [user]
  (insert! :invitations
           {:_id (generate-invitation-code)
            :created-at (java.util.Date.)
            :from user
            :used false}))

(defn consume-invitation! [id new-user]
  (update! :invitations {:_id id}
           {:$set {:used-at (java.util.Date.)
                   :to new-user
                   :used true}}))

(defn invitation-consumed? [id]
  (if-let [invite (retrieve-invitation id)]
    (get invite :used true)))

(defn invitation-used-by [id]
  (:to (fetch-one :invitations :where {:_id id})))