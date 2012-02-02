(ns soipf.server
  (:use [ring.middleware.anti-forgery :only [wrap-anti-forgery]])
  (:require [noir.server :as server]
            [soipf.config :as config]
            [somnium.congomongo :as mongo]))

(server/load-views "src/soipf/views/")

(defn wrap-mongo [handler]
  (fn [request]
    (mongo/with-mongo (config/get-mongo-connection)
      (do
        (handler request)))))

(server/add-middleware wrap-mongo)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'soipf})))
