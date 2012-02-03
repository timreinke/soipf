(ns soipf.models.thread
  (:require [noir.validation :as vali]
            [noir.session :as session]
            [clj-time.core :as time]
            [clj-time.coerce :as coerce])
  (:use soipf.db
        soipf.format
        somnium.congomongo))

(def thread-metadata
  [:title :author :created-at :updated-at :reply-count])

(defn valid? [title body]
  (vali/rule (vali/has-value? title) [:title "You must have a title"])
  (vali/rule (vali/has-value? body) [:body "You must have a body"])
  (not (vali/errors? :title :body)))

(defn get-thread-listing []
  (fetch :threads :only thread-metadata :limit 20 :sort {:updated-at -1}))

(defn create-thread! [{:keys [title body author]}]
  (when (valid? title body)
    (let [now (java.util.Date.)]
      (insert! :threads {:_id (new-id "threads")
                         :title title :author author
                         :created-at now :updated-at now
                         :reply-count 0
                         :posts [{:author author
                                  :created-at now
                                  :content (markdownify body)
                                  :raw-content body}]}))))

(defn add-reply! [{:keys [id body author]}]
  (let [now (java.util.Date.)
        login (session/get :login "Anonymous")]
    (update! :threads {:_id id}
             {:$push {:posts {:author author
                              :created-at now
                              :content (markdownify body)
                              :raw-content body}}
              :$set {:updated-at now}
              :$inc {:reply-count 1}})))

(defn retrieve-thread
  ([id]
     (fetch-one :threads :where {:_id id}))
  ([id slice]
     (fetch-one :threads :where {:_id id})))
