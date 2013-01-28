(ns soipf.resources.user
  (:require [soipf.resources :refer [get-context authorized-for?]]
            [soipf.db :refer [fetch]]
            [soipf.util :refer [defkeys]]
            [compojure.core :refer [defroutes GET]]
            [compojure.response :refer [render]]))

(defkeys :soipf.user
  :login
  :password-hash)

(def default-view #{login})
(def auth-view #{login password-hash})

(defn get-user
  ([login*]
     (get-user login* auth-view))
  ([login* fields]
     (let [user (get-context [:session :current-user])
           authorized? (authorized-for? user :get fields)]
       (if (not authorized?)
         (throw (Exception.)))
       (let [res (fetch {login login*} fields)]
         (println (str "result: " res))
         res))))

(defroutes routes
  (GET "/:login" [login]
       (str (get-user login))))