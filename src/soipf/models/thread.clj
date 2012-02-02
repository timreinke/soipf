(ns soipf.models.thread
  (:require [noir.validation :as vali]
            [noir.session :as session]
            [clj-time.core :as time]
            [clj-time.coerce :as coerce])
  (:use soipf.db
        somnium.congomongo))

(def thread-metadata
  [:title :author :created-at :updated-at :reply-count])

(defn valid? [title body]
  (vali/rule (vali/has-value? title) [:title "You must have a title"])
  (vali/rule (vali/has-value? body) [:body "You must have a body"])
  (not (vali/errors? :title :body)))

(defn get-thread-listing []
  (fetch :threads :only thread-metadata :limit 20 :sort {:updated-at -1}))

(defn create-thread [title body]
  (when (valid? title body)
    (let [now (java.util.Date.)
          login (session/get :login "Anonymous")]
      (insert! :threads {:_id (new-id "threads")
                         :title title :author login
                         :created-at now :updated-at now
                         :reply-count 0
                         :posts [{:author login
                                  :created-at now
                                  :content body}]}))))

(defn add-post [thread-id body]
  (let [now (java.util.Date.)
        login (session/get :login "Anonymous")]
    (update! :threads {:_id thread-id}
             {:$push {:posts {:author login
                              :created-at now
                              :content body}}})))

(defn retrieve-thread
  ([id]
     (fetch-one :threads :where {:_id id}))
  ([id slice]
     (fetch-one :threads :where {:_id id})))
