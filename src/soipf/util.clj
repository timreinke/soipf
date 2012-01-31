(ns soipf.util
  (:require [soipf.config :as config]
            [somnium.congomongo :as mongo]))

(defmacro query [& body]
  `(mongo/with-mongo (config/get-mongo-connection)
     ~@body))

(defmacro defquery [name args & body]
  `(defn ~name ~args
     (query ~@body)))