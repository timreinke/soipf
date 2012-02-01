(ns soipf.views.common
  (:require [noir.validation :as vali]
            [clj-time [format :as format]
                      [coerce :as coerce]])
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5 link-to]]))

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

(defpartial navigation []
  [:ul.nav.list
   [:li.active [:a "Home"]]
   [:li [:a "Tomorrow"]]])

(defpartial layout [& content]
            (html5
              [:head
               [:title "soipf"]
               (include-css "/css/bootstrap.css")
               (include-css "/css/bootstrap.responsive.css")]
              [:body
               [:div.navbar
                [:div.navbar-inner
                 [:div.container
                  (link-to {:class "brand"} "#" "soipf")]]]
               [:div.container
                [:div.row
                 [:div.span2 (navigation)]
                 [:div.span10 content]]]
               (include-js "http://code.jquery.com/jquery-1.7.1.js")
               (include-js "/js/bootstrap.js")]))
