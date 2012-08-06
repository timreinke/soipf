(ns soipf.paginate
  (:use [clojure.string :only [join]]
        [hiccup.util :only [url-encode]]
        noir.request))

(defn get-limit []
  (Integer/parseInt (get-in (ring-request) [:params :limit] "20")))

(defn get-skip
  ([]
     (Integer/parseInt (get-in (ring-request) [:params :skip] "0")))
  ([page]
     (* (get-limit)
        (- page 1))))

(defn page-map [skip limit]
  {:skip skip
   :limit limit})

(defn current-page []
  (+ 1 (int (/ (get-skip) (get-limit)))))

(defn page-count [total-items]
  (+ 1 (int (/ (- total-items 1) (get-limit)))))

(defn page-query-by-index [index]
  (page-map
   (* (get-limit)
      (int (/ index (get-limit))))
   (get-limit)))

(defn query-str-by-index [index]
  (url-encode (page-query-by-index index)))