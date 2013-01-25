(ns soipf.resources.user
  (:require [soipf.resources :refer [fetch get-context authorized-for?]]))

(def default-view #{:id :login :created-at})
(def auth-view #{:password-hash :salt})

(defn get-user
  ([login]
     (get-user login {}))
  ([login query-params]
     (let [user (get-context [:session :current-user])
           fields (or (query-params :fields) default-view)
           authorized? (authorized-for? user :get fields)]
       (if (not authorized?)
         (throw (Exception.)))
       (fetch :users login {:fields fields}))))