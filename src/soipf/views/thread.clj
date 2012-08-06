(ns soipf.views.thread
  (:require [noir.validation :as vali])
  (:use [clojure.string :only [join]]
        noir.core
        noir.request
        [noir.response :only [redirect status json]]
        [noir.session :only [put!]]
        hiccup.element
        hiccup.form
        hiccup.page
        [hiccup.util :only [url]]
        soipf.paginate
        soipf.views.common
        [soipf.format :only [date-str]]
        soipf.models.thread
        [soipf.models.user :only [logged-in?]]))

(defn- assoc-index [skip ms]
  (map (fn [i m]
         (assoc m :index (+ i skip)))
       (range) ms))

(defn mark-unread [threads]
  (if-let [user (logged-in?)]
    (let [user-id (:_id user)
          read-posts (last-posts-read user-id (map :_id threads))]
      (map (fn [t]
             (let [last-read (get read-posts (:_id t) 0)]
               (assoc t
                 :last-read last-read
                 :unread (- (:reply-count t)
                            last-read))))
           threads))
    threads))

(defn assert-logged-in []
  (when-not (logged-in?)
    (put! :redirected-from ((ring-request) :uri))
    (redirect "/login")))

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

(defpartial display-post [last-read post]
  [:div {:id (:index post)
         :class (join " "  ["post" (when (>= (:index post) last-read)
                                     "unread")])}
   [:div.metadata
    [:span.author
     (get-in post [:author :login])]
    " at "
    (date-str (post :created-at))]
   (post :content)])

(defpartial list-thread [thread]
  (let [unread-index (- (:reply-count thread)
                        (:unread thread))]
    [:div {:class (join " "  ["item" (when-not (zero? (:unread thread))
                                       "unread")])}
     [:div.inner (link-to (url (url-for show-thread {:id (thread :_id)})
                               "?" (query-str-by-index unread-index)
                               "#" (dec unread-index))
                          (thread :title))
      (when-not (zero? (:unread thread))
        [:span.info (str (:unread thread) " unread")])
      [:div.info "by " (get-in thread [:author :login])]]]))

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
  (let [pagination (pagination-html (:reply-count thread))]
    [:div pagination
     [:div.thread
      [:div.heading
       (link-to (url-for show-thread {:id (thread :_id)}) (thread :title))]
      [:div.content
       (map #(display-post (:last-read thread) %)
            (assoc-index (get-skip) (thread :posts)))]
      (reply-form (thread :_id) body)]
     pagination]))

(pre-route "/" {}
           (assert-logged-in))
(pre-route "/thread*" {}
           (assert-logged-in))

(defpage "/" []
  (let [threads (mark-unread (get-thread-listing))]
    (layout
     [:div.row
      (link-to {:class "action"} "/thread" "new thread")]
     [:div.threads (map list-thread threads)])))

(defpage "/thread" {:as t}
  (layout (new-thread t)))

(defpage [:post "/thread"] {:keys [title body] :as t}
  (if-let [thread (create-thread! (assoc t :author (logged-in?)))]
    (redirect (url-for show-thread {:id (:_id thread)}))
    (render "/thread" t)))

(defpage show-thread "/thread/:id" {:keys [id body]}
  (if-let [{:keys [_id title author created-at posts] :as thread} (retrieve-thread id)]
    (let [thread (first (mark-unread [thread]))]
      (when-let [user (logged-in?)]
        (read-thread! (:_id user) id (+ (count posts)
                                        (get-skip))))
      (layout (display-thread thread body)))
    (status 404 (layout [:h1 "Thread not found"]))))

(defpage reply-to-thread [:post "/thread/:id"] {:keys [id body] :as args}
  (if-let [{:keys [title posts reply-count]} (retrieve-thread id)]
    (if (add-reply! {:thread-id id
                     :author (logged-in?)
                     :body body})
      (redirect (url (url-for show-thread {:id id})
                     (page-query-by-index
                      ;; inc for new reply
                      (inc reply-count))))
      (render show-thread {:id id :body body}))
    (redirect "/")))
