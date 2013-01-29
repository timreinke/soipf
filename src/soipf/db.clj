(ns soipf.db
  (:require [datomic.api :as d :refer [q db]]))

(def conn nil)

(defn set-connection-globally [uri]
  (alter-var-root conn (fn [_] (d/connect uri))))

(defn make-where
  "Given a map `matches` of attributes -> values, generate a list
   of datalog where clauses on entity `entity-symbol`

   (make-where '?e {:app.user/login \"user\"})
   == '((?e :app.user/login \"user\"))"
  [entity-symbol matches]
  (map (fn [[k v]]
         (list entity-symbol k v))
       matches))

(defn make-query [matches]
  (list* :find '?e :where (make-where '?e matches)))

(defn fetch
  "Returns the first result found by the map `matches` as a map"
  [matches fields]
  (let [results (q (make-query matches) (db conn))
        eid (ffirst results)
        entity (-> conn db (d/entity eid))]
    (into {} (map
              (fn [field]
                [field (field entity)])
              fields))))