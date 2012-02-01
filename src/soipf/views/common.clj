(ns soipf.views.common
  (:require [noir.validation :as vali])
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

(defn error-class [field]
  (str "control-group"
       (if (vali/errors? field)
         " error")))

(defn error-help [field]
  (vali/on-error field (fn [es] (list [:br] [:span.help-inline (first es)]))))


(defpartial error-text [errors]
  [:div.alert (interpose [:br] errors)])

(defpartial layout [& content]
            (html5
              [:head
               [:title "soipf"]
               (include-css "/css/bootstrap.css")
               (include-css "/css/bootstrap.responsive.css")]
              [:body
               [:div.container content]
               (include-js "http://code.jquery.com/jquery-1.7.1.js")
               (include-js "/js/bootstrap.js")]))
