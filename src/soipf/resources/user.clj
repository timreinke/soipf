(ns soipf.resources.user
  (:require [soipf.resources :refer [get-context authorized-for?]]
            [soipf.db :refer [fetch]]
            [soipf.util :refer [defkeys]]))

(defkeys :soipf.user
  :login
  :password-hash)

(def default-view #{login})
(def auth-view #{:password-hash})

(defn get-user
  ([login*]
     (get-user login* default-view))
  ([login* fields]
     (let [user (get-context [:session :current-user])
           authorized? (authorized-for? user :get fields)]
       (if (not authorized?)
         (throw (Exception.)))
       (fetch {login login*} fields))))