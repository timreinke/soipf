(ns soipf.views.thread
  (:require [noir.validation :as vali])
  (:use noir.core
        [noir.response :only [redirect status json]]
        [hiccup.form-helpers]
        [hiccup.page-helpers]
        [soipf.views.common]
        [soipf.models.thread :only [get-thread-listing create-thread retrieve-thread]]
        [soipf.models.user :only [logged-in?]]))

(defpartial new-thread [{:keys [title body]}]
  (form-to {:class "form-horizontal"} [:post "/thread"]
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
   [:td (link-to (url-for show-thread {:id _id}) title)]
   [:td author]
   [:td (date-str created-at)]
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
   [:div.row
    (link-to {:class "btn primary pull-right"} "/thread" "New Thread")]
   (thread-listing (get-thread-listing))))

(pre-route "/thread" {}
           (when-not (logged-in?)
             (redirect "/login")))

(defpage "/thread" {:as t}
  (layout (new-thread t)))

(defpage [:post "/thread"] {:keys [title body] :as t}
  (if-let [thread (create-thread title body)]
    (redirect (url-for show-thread {:id (:_id thread)}))
    (render "/thread" t)))

(defpage show-thread "/thread/:id" {:keys [id]}
  (if-let [{:keys [_id title author created-at posts] :as thread} (retrieve-thread id)]
    (layout )
    (status 404 (layout "Thread not found"))))