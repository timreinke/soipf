(ns soipf.resources.user
  (:require [soipf.resources :refer [get-context authorized-for?]]
            [soipf.db :refer [fetch put]]
            [soipf.util :refer [defkeys]]
            [compojure.core :refer [defroutes GET]]
            [compojure.response :refer [render]]))

;; I'd rather like the following to be more like
;; (defviews :soipf.user
;;   default [:login]
;;   authentication [:login :password-hash])
;; (fetch-view authentication :where [:login "tim"])

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
         (throw (ex-info "Unauthorized access attempt" {:status 403})))
       (fetch login login* fields))))

(defn post-user [user]
  (let [existing (get-user (login user))]
    (when existing
      (throw (ex-info "Resource already exists" {:status 409})))
    (put user)))


(defroutes routes
  (GET "/:login" [login]
       (str (get-user login))))