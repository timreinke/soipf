(ns soipf.app
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [soipf.resources.user :as user]
            [ring.middleware.refresh :refer [wrap-refresh]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defroutes app-routes
  (context "/users" []
           user/routes))

(def handler
  (handler/site app-routes))

(def dev-handler
  (-> handler
      wrap-refresh
      wrap-reload))