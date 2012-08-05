(ns soipf.views.thread
  (:refer-clojure :exclude [get swap!])
  (:require [noir.validation :as vali])
  (:use noir.core
        noir.request
        noir.session
        [noir.response :only [redirect status json]]
        hiccup.element
        hiccup.form
        hiccup.page
        [hiccup.util :only [url]]
        soipf.paginate
        soipf.views.common
        [soipf.format :only [date-str]]
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
              {:name "body" :rows 6 :cols 50}
              body]
             (error-help :body)]]
           [:div.form-actions
            [:button.click-once {:type "submit"} "Create Thread"]]))

(defpartial display-post [post]
  [:div.post
   [:div.metadata
    [:span.author (get-in post [:author :login])]
    " at "
    (date-str (post :created-at))]
   (post :content)])

(defpartial list-thread [thread]
  [:div.item (link-to (url-for show-thread {:id (thread :_id)})
                      (thread :title))
   [:div.info "by " (get-in thread [:author :login])]])

(defpage "/" []
  (let [threads (get-thread-listing)]
    (layout
     [:div.row
      (link-to {:class "btn btn-primary"} "/thread" "New Thread")]
     (map list-thread threads))))

(defn assert-logged-in []
  (when-not (logged-in?)
    (put! :redirected-from ((ring-request) :uri))
    (redirect "/login")))

(pre-route "/" {}
           (assert-logged-in))
(pre-route "/thread*" {}
           (assert-logged-in))

(defpage "/thread" {:as t}
  (layout (new-thread t)))

(defpage [:post "/thread"] {:keys [title body] :as t}
  (if-let [thread (create-thread! (assoc t :author (logged-in?)))]
    (redirect (url-for show-thread {:id (:_id thread)}))
    (render "/thread" t)))

(defpartial reply-form [id body]
  (form-to {:class "form-horizontal well"} [:post (url-for reply-to-thread {:id id})]
           [:div {:class (error-class :body)}
            [:div.controls
             [:textarea#body.input-xlarge
              {:name "body" :rows 6 :cols 50}
              body]
             (error-help :body)]]
           [:div.form-actions
            [:button.click-once {:type "submit"} "Add Reply"]]))

(defpartial display-thread [thread body]
  (let [pagination (pagination-html (inc (:reply-count thread)))]
    [:div pagination
     [:div.thread
      [:div.heading
       (link-to (url-for show-thread {:id (thread :_id)}) (thread :title))]
      [:div.content
       (map display-post (thread :posts))]
      (reply-form (thread :_id) body)]
     pagination]))

(defpage show-thread "/thread/:id" {:keys [id body]}
  (if-let [{:keys [_id title author created-at posts] :as thread} (retrieve-thread id)]
    (layout (display-thread thread body))
    (status 404 (layout [:h1 "Thread not found"]))))

(defpage reply-to-thread [:post "/thread/:id"] {:keys [id body] :as args}
  (if-let [{:keys [title posts reply-count]} (retrieve-thread id)]
    (do (if (add-reply! {:thread-id id
                         :author (logged-in?)
                         :body body})
          (redirect (url (url-for show-thread {:id id}) {"skip" (get-skip
                                                        (page-count
                                                         (inc reply-count)))
                                                "limit" (get-limit)}))
          (render show-thread {:id id :body body})))
    (redirect "/")))
