(ns soipf.test.paginate
  (:use clojure.test
        midje.sweet
        soipf.paginate))

(tabular
  (fact "Can go from an item index to a page"
    (page-query-by-index ?index) => ?map
    (provided
     (get-limit) => 20))
  ?index        ?map
  0             (page-map 0 20)
  19            (page-map 0 20)
  20            (page-map 20 20))