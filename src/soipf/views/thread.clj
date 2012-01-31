(ns soipf.views.thread
  (:require [soipf.views.common :as common]
            [noir.validation :as vali])
  (:use [noir.core :only [defpage defpartial pre-route render]]
        [noir.response :only [redirect]]
        [hiccup.form-helpers]
        [soipf.models.thread :only [get-thread-listing create-thread]]
        [soipf.models.user :only [logged-in?]]))

(defn error-class [field]
  (str "control-group"
       (if (vali/errors? field)
         " error")))

(defn error-help [field]
  (vali/on-error field (fn [es] (list [:br] [:span.help-inline (first es)]))))

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