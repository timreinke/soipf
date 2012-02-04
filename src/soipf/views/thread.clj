(ns soipf.views.thread
  (:require [noir.validation :as vali])
  (:use noir.core
        [noir.response :only [redirect status json]]
        hiccup.form-helpers
        hiccup.page-helpers
        soipf.views.common
        [soipf.models.thread :only [create-thread! add-reply! get-thread-listing retrieve-thread]]
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
            [:div.controls
             [:textarea#body.input-xlarge
              {:name "body" :rows 6 }
              body]
             (error-help :body)]]
           [:div.form-actions
            [:button.btn.btn-primary {:type "submit"} "Create Thread"]]))

(defpartial list-thread [{:keys [_id title author created-at updated-at reply-count]}]
  [:tr
   [:td (link-to (url-for show-thread {:id _id}) title)]
   [:td (:login author)]
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
    (link-to {:class "btn btn-primary"} "/thread" "New Thread")]
   (thread-listing (get-thread-listing))))

(pre-route "/thread*" {}
           (when-not (logged-in?)
             (redirect "/login")))

(defpage "/thread" {:as t}
  (layout (new-thread t)))

(defpage [:post "/thread"] {:keys [title body] :as t}
  (if-let [thread (create-thread! (assoc t :author (logged-in?)))]
    (redirect (url-for show-thread {:id (:_id thread)}))
    (render "/thread" t)))

(defpartial thread-heading [title]
  [:h1.thread title])

(defpartial display-post [{:keys [author created-at content]}]
  [:div.post.well
   [:div.heading [:span.author (:login author)] " at " (date-str created-at)]
   [:hr]
   [:div.content content]])

(defpartial reply-form [id body]
  (form-to {:class "form-horizontal well"} [:post (url-for reply-to-thread {:id id})]
           [:legend "Add Reply"]
           [:div {:class (error-class :body)}
            [:label {:for "body"} "Body"]
            [:div.controls
             [:textarea#body.input-xlarge
              {:name "body" :rows 6 }
              body]
             (error-help :body)]]
           [:div.form-actions
            [:button.btn.btn-primary {:type "submit"} "Add Reply"]]))

(defpartial display-thread [{:keys [title posts]}]
  (thread-heading title)
  [:div.posts
   (for [p posts]
     (display-post p))])

(defpage show-thread [:get "/thread/:id"] {:keys [id body]}
  (if-let [{:keys [_id title author created-at posts] :as thread} (retrieve-thread id)]
    (layout (display-thread thread)
            (reply-form id body))
    (status 404 (layout [:h1 "Thread not found"]))))

(defpage reply-to-thread [:post "/thread/:id"] {:keys [id body] :as args}
  (if-let [{:keys [_id title posts]} (retrieve-thread id -5)]
    (do (add-reply! (assoc args :author (logged-in?)))
        (render show-thread {:id id}))
    (redirect "/")))
