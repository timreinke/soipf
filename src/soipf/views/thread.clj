(ns soipf.views.thread
  (:require [soipf.views.common :as common])
  (:use [noir.core :only [defpage defpartial pre-route render]]
        [noir.response :only [redirect]]
        [hiccup.form-helpers]
        [soipf.models.thread :only [get-thread-listing create-thread]]
        [soipf.models.user :only [logged-in?]]))

(defpartial new-thread [{:keys [title body]}]
  (form-to {:class "well"} [:post "/thread/new"]
           [:legend "New Thread"]

           [:label {:for "title"} "Title"]
           (text-field :title title)

           [:label {:for "body"} "Body"]
           ;; TODO: create this style

           [:textarea#body.input-xxxlarge
            {:name "body" :rows 6 :style "width: 100%; max-width: 250px;"}
            body]
           [:div
            [:button.btn.primary {:type "submit"} "Create Thread"]]))

(defpartial display-thread-listing [threads]
  (if (empty? threads)
    [:div "There are no threads here!  You should make one"]))

(defpage "/" []
  (common/layout
   (display-thread-listing (get-thread-listing))))


(pre-route "/thread/new" {}
  (when-not (logged-in?)
    (redirect "/login")))

(defpage "/thread/new" {:as t}
  (common/layout [:div.row
                  [:div.span6.offset3 (new-thread t)]]))

(defpage [:post "/thread/new"] {:keys [title body] :as t}
  (if (create-thread title body)
    (common/layout "success!")
    (render "/thread/new" t)))