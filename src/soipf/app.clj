(ns soipf.app
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [soipf.resources.user :as user]))

(defroutes app-routes
  (context "/users" []
           user/routes))

(def handler
  (handler/site app-routes))