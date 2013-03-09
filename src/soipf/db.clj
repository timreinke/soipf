(ns soipf.db
  (:require [datomic.api :as d :refer [q db]]))

(declare conn)

(defn set-connection-globally [uri]
  (alter-var-root #'conn (fn [_] (d/connect uri))))

(defn fetch
  "Returns the first result found by the map `matches` as a map"
  [key value fields]
  (let [results (q '[:find ?e :in $ ?key ?value :where [?e ?key ?value]]
                   (db conn) key value)
        eid (ffirst results)]
    (if eid
      (let [entity (-> conn db (d/entity eid))]
        (into {} (map
                  (fn [field]
                    [field (field entity)])
                  fields))))))

(defn put [thing]
  (d/transact conn
              [(merge {:db/id #db/id [db.part/soipf]} thing)]))