(ns soipf.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

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
