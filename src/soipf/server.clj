(ns soipf.server
  (:use [ring.middleware.anti-forgery :only [wrap-anti-forgery]]
        [clojure.tools.logging :only [info]]
        [hozumi.mongodb-session :only [mongodb-store]])
  (:require [noir.server :as server]
            [soipf.db :as db]
            [somnium.congomongo :as mongo])
  (:gen-class
   :main true))

(server/load-views-ns 'soipf.views)

(mongo/mongo! :db "soipf")

(defn wrap-mongo [handler]
  (fn [request]
    (mongo/with-mongo (db/get-mongo-connection)
      (handler request))))


(defn update-keys [m [& ks] f]
  (reduce merge m (map (fn [k]
                         (hash-map k (f (get m k {}))))
                       ks)))

(def private-data ^{:private true}
  [:password :password-confirm])
(let [keys (concat private-data (map name private-data))]
  (defn sanitize [request]
    (update-keys request
                 [:query-params :form-params :params]
                 (fn [m] (apply dissoc m keys)))))

(server/add-middleware wrap-mongo)
(server/add-middleware (fn [handler]
                         (fn [request]
                           (let [request-id (str (java.util.UUID/randomUUID))
                                 start (System/nanoTime)]
                             (info request-id (sanitize request))
                             (let [resp (handler request)]
                               (info request-id (:uri request) (:status resp)
                                     "took"
                                     (/ (double (- (System/nanoTime) start))
                                        1000000.0)
                                     "ms")
                               resp)))))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :session-store (mongodb-store)
                        :ns 'soipf})))
