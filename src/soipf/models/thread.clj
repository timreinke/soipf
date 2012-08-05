(ns soipf.models.thread
  (:require [noir.validation :as vali]
            [noir.session :as session]
            [clj-time.core :as time]
            [clj-time.coerce :as coerce])
  (:use soipf.db
        soipf.format
        soipf.paginate
        somnium.congomongo
        [hiccup.util :only [escape-html]]))

(def author-keys
  ^{:private true
    :doc "The keys of author to put in the thread object"}
  [:_id :login])

(defn valid-thread? [title body]
  (vali/rule (vali/has-value? title) [:title "You must have a title"])
  (vali/rule (vali/max-length? title 140) [:title "Title must be less than 140 characters"])
  (vali/rule (vali/has-value? body) [:body "You must have a body"])
  (not (vali/errors? :title :body)))

(defn valid-post? [body]
  (vali/rule (vali/has-value? body) [:body "You must have a body"])
  (not (vali/errors? :body)))

(defn get-thread-listing []
  (fetch :threads :limit 20 :sort {:updated-at -1}))

(defn create-thread! [{:keys [title body author]}]
  (when (valid-thread? title body)
    (let [now (java.util.Date.)
          thread-id (new-id "threads")
          author (select-keys author author-keys)]
      (insert! :posts {:_id (new-id "posts")
                       :thread-id thread-id
                       :author author
                       :created-at now
                       :content (markdownify body)
                       :raw-content body})
      (insert! :threads {:_id thread-id
                         :title (escape-html title) :author author
                         :created-at now :updated-at now
                         :reply-count 1}))))

(defn add-reply! [{:keys [thread-id body author]}]
  (when (valid-post? body)
    (let [now (java.util.Date.)
          login (session/get :login "Anonymous")]
      (insert! :posts {:_id (new-id "posts")
                       :thread-id thread-id
                       :author (select-keys author author-keys)
                       :created-at now
                       :content (markdownify body)
                       :raw-content body})
      (update! :threads {:_id thread-id}
               {:$set {:updated-at now}
                :$inc {:reply-count 1}}))))

(defn retrieve-thread [thread-id]
  (if-let [thread (fetch-one :threads :where {:_id thread-id})]
    (assoc thread
      :posts (fetch :posts :where {:thread-id thread-id}
                    :skip (get-skip)
                    :limit (get-limit)))))