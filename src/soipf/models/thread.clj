(ns soipf.models.thread
  (:require [noir.validation :as vali]
            [noir.session :as session]
            [clj-time :as time])
  (:use [soipf.util :only [defquery]]
        somnium.congomongo))

(def thread-metadata
  [:title :author :created-at :updated-at :reply-count])

(defn valid? [title body]
  (vali/rule (vali/has-value? title) [:title "You must have a title"])
  (vali/rule (vali/has-value? body) [:body "You must have a body"])
  (not (vali/errors? :title :body)))

(defquery get-thread-listing []
  (fetch :threads :only thread-metadata :limit 20))

(defquery create-thread [title body]
  (when (valid? title body)
    (let [now (time/now)
          login (session/get :login "Anonymous")]
      (insert! :threads {:title title :author login
                         :created-at now :updated-at now
                         :reply-count 0
                         :posts [{:author login
                                  :created-at now
                                  :content body}]}))))