(ns soipf.config)

(def config*
  {:db
   {:uri "datomic:free://localhost:4334/soipf"}})

(defn config [& keys]
  (get-in config* keys))