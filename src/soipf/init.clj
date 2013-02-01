(ns soipf.init
  (:require [soipf.config :refer [config]]
            [soipf.db :as db]
            [datomic.api :refer [connect]]))

(defn init []
  (db/set-connection-globally (config :db :uri)))