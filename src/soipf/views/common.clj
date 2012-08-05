(ns soipf.views.common
  (:require [soipf.models.user :as user]
            [noir.validation :as vali])
  (:use [clojure.string :only [join]]
        [hiccup.core :only [html]]
        hiccup.element
        hiccup.page
        hiccup.util
        [noir.core :only [defpartial]]
        noir.request
        soipf.paginate))

(defn error-class [field]
  (str "control-group"
       (if (vali/errors? field)
         " error")))

(defn error-help [field]
  (vali/on-error field (fn [es] [:div.help (first es)])))

(defpartial error-text [errors]
  [:div.alert (interpose [:br] errors)])

(defpartial head [title]
   [:head
    [:title "soipf/" title]
    (include-css "/css/reset.css"
                 "/css/main.css")])

(defn- uri-with-query-params [uri query-params]
  (str uri "?"
       (join "&"
             (map (fn [entry]
                    (join "=" (map (comp url-encode str) entry)))
                  query-params))))

(defn page-range []
  (map (fn [n]
         {:n n
          "skip" (get-skip n)
          "limit" (get-limit)})
       (map inc (range))))

(defn page-link [page]
  (if (= (:n page) (current-page))
    [:span.page-link (:n page)]
    (let [{:keys [uri query-params]} (ring-request)
          query-params (merge query-params (dissoc page :n))]
      (link-to {:class "page-link"} (url
                 uri
                 query-params)
               (:n page)))))

(defpartial pagination-html [total-items]
  [:div.pagination "Page " (take (page-count total-items) (map page-link (page-range)))])

(defpartial user-bar []
  (when-let [user (user/logged-in?)]
    [:span.pull-right.user-bar
     [:a (:login user)]
     "/"
     (link-to "/logout" "logout")]))

(defpartial page-header []
  [:div.top
   (link-to {:class :title} "/" "soipf")
   (user-bar)])

(defpartial page-footer []
  [:div.footer [:p "soipf"]])

(defpartial layout [& content]
            (html5
             (head "soipf")
             [:body
              [:div#wrapper
               (page-header)
               [:div#content
                content]
               (map include-js
                    ["/js/jquery.js"
                     "/js/app.js"])
               (page-footer)]]))