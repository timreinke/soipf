(ns soipf.init
  (:require [soipf.config :refer [config]]
            [soipf.db :as db]
            [datomic.api :refer [connect]]))

(defn init []
  (alter-var-root #'db/conn (fn [_]
                              (connect (config :db :uri)))))
