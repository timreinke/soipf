(ns soipf.models.thread
  (:require [noir.validation :as vali]
            [noir.session :as session])
  (:use [soipf.util :only [defquery]]
        somnium.congomongo))

(def thread-metadata
  [:title :author :date-posted :date-updated :post-count])

(defn valid? [title body]
  (vali/rule (vali/has-value? title) [:title "You must have a title"])
  (vali/rule (vali/has-value? body) [:body "You must have a body"])
  (not (vali/errors? :title :body)))

(defquery get-thread-listing []
  (fetch :threads :only thread-metadata))

(defquery create-thread [title body]
  (when (valid? title body)
    (let [now (java.util.Date.)]
      (insert! :threads {:title title :created-at now
                         :posts [{:author (session/get :login "Anonymous")
                                  :created-at now
                                  :content body}]}))))