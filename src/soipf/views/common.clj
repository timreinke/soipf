(ns soipf.views.common
  (:require [soipf.models.user :as user]
            [noir.validation :as vali])
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers]
        [hiccup.form-helpers]))

(defn error-class [field]
  (str "control-group"
       (if (vali/errors? field)
         " error")))

(defn error-help [field]
  (vali/on-error field (fn [es] (list [:br] [:span.help-inline (first es)]))))

(defpartial error-text [errors]
  [:div.alert (interpose [:br] errors)])

(defpartial head [title]
   [:head
    [:title "soipf/" title]
    (include-css "/css/reset.css"
                 "/css/main.css")])

(defpartial page-header []
  [:div.top
   (link-to {:class :title} "/" "soipf")
   (user-bar)])

(defpartial user-bar []
  (if-let [user (user/logged-in?)]
    [:span.pull-right
     [:a (:login user)]
     "/"
     (link-to "/logout" "logout")]
    (list
     (form-to {:class "navbar-search pull-right"} [:post "/login"]
              (text-field {:class "input-small"
                           :placeholder "Username"} "login")
              (password-field {:class "input-small"
                               :placeholder "Password"} "password")
              (submit-button {:style "position: absolute; left: -9999px; width: 1px; height: 1px"} "")))))

(defpartial layout [& content]
            (html5
             (head "soipf")
             [:body
              (page-header)
              [:div#wrapper
               [:div#content
                content]]
              (include-js "/js/jquery.js")]))
