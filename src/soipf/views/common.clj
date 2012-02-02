(ns soipf.views.common
  (:require [soipf.models.user :as user]
            [noir.validation :as vali]
            [clj-time [format :as format]
                      [coerce :as coerce]])
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers]
        [hiccup.form-helpers]))

(defn error-class [field]
  (str "control-group"
       (if (vali/errors? field)
         " error")))

(defn error-help [field]
  (vali/on-error field (fn [es] (list [:br] [:span.help-inline (first es)]))))

(def date-format (format/formatter "yyyy-MM-dd hh:mm"))

(defn date-str [date]
  (if (= (class date) org.joda.time.DateTime)
    (format/unparse date-format date)
    (date-str (coerce/from-date date))))

(defpartial error-text [errors]
  [:div.alert (interpose [:br] errors)])

(defpartial user-bar []
  (if-let [login (user/logged-in?)]
    [:ul.nav.pull-right
     [:li [:a login]]
     [:li (link-to "/logout" "logout")]]
    (list
     (form-to {:class "navbar-search pull-right"} [:post "/login"]
              (text-field {:class "input-small" :style "margin-right: 5px"
                           :placeholder "Username"} "login")
              (password-field {:class "input-small" :style "margin-right: 5px"
                               :placeholder "Password"} "password")
              (submit-button {:style "position: absolute; left: -9999px; width: 1px; height: 1px"} "")))))

(defpartial layout [& content]
            (html5
              [:head
               [:title "soipf"]
               (include-css "/css/app.css")]
              [:body
               [:div.navbar
                [:div.navbar-inner
                 [:div.container
                  (link-to {:class "brand"} "/" "soipf")
                  (user-bar)]]]
               [:div.container
                [:div.row
                 [:div.span12 content]]]
               (include-js "http://code.jquery.com/jquery-1.7.1.js")
               (include-js "/js/bootstrap.js")]))
