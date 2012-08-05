(ns soipf.paginate
  (:use noir.request))

(defn get-limit []
  (Integer/parseInt (get-in (ring-request) [:params :limit] "20")))

(defn get-skip
  ([]
     (Integer/parseInt (get-in (ring-request) [:params :skip] "0")))
  ([n]
     (* (get-limit)
        (- n 1))))

(defn current-page []
  (+ 1 (int (/ (get-skip) (get-limit)))))

(defn page-count [total-items]
  (+ 1 (int (/ (- total-items 1) (get-limit)))))
