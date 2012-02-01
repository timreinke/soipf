(ns soipf.views.thread
  (:require [noir.validation :as vali])
  (:use [noir.core :only [defpage defpartial pre-route render]]
        [noir.response :only [redirect]]
        [hiccup.form-helpers]
        [soipf.views.common]
        [soipf.models.thread :only [get-thread-listing create-thread]]
        [soipf.models.user :only [logged-in?]]))

(defpartial new-thread [{:keys [title body]}]
  (form-to {:class "form-horizontal"} [:post "/thread/new"]
           [:legend "New Thread"]
           [:div {:class (error-class :title)}
            [:label {:for "title"} "Title"]
            [:div.controls
             (text-field {:class "input-xlarge"} :title title)
             (error-help :title)]]

           [:div {:class (error-class :body)}
            [:label {:for "body"} "Body"]
            ;; TODO: create this style
            [:div.controls
             [:textarea#body.input-xlarge
              {:name "body" :rows 6 }
              body]
             (error-help :body)]]
           [:div.form-actions
            [:button.btn.primary {:type "submit"} "Create Thread"]]))

(defpartial list-thread [{:keys [_id title author created-at updated-at reply-count]}]
  [:tr
   [:td title]
   [:td author]
   [:td (when created-at (.. (java.text.DateFormat/getDateInstance) (format created-at)))]
   [:td reply-count]])

(defpartial thread-listing [threads]
  (if (empty? threads)
    [:div "There are no threads here!  You should make one"]
    [:table.table.table-striped
     [:thead
      [:tr
       [:th "Title"]
       [:th "Author"]
       [:th "Created"]
       [:th "Replies"]]]
     [:tbody
      (for [t threads]
        (list-thread t))]]))

(defpage "/" []
  (layout
   (thread-listing (get-thread-listing))))

(pre-route "/thread/new" {}
           (when-not (logged-in?)
             (redirect "/login")))

(defpage "/thread/new" {:as t}
  (layout [:div.row
           [:div.span2
            [:ul.nav.list
             [:li.active [:a "Home"]]
             [:li [:a "Tomorrow"]]]]
           [:div.span10 (new-thread t)]]))

(defpage [:post "/thread/new"] {:keys [title body] :as t}
  (if (create-thread title body)
    (layout "success!")
    (render "/thread/new" t)))