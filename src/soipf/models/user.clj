(ns soipf.models.user
  (:require [noir.util.crypt :as crypt]
            [noir.session :as session]
            [noir.validation :as vali]
            [clj-time.core :as time])
  (:use [soipf.db :only [new-id]]
        somnium.congomongo))

(defn valid? [login password password-confirm]
  (if (fetch-one :users :where {:login login})
    (vali/set-error :login "Login already exists"))
  (if (not (= password password-confirm))
    (vali/set-error :password "Passwords don't match"))
  (if (vali/errors?)
    false
    true))

(defn create-user! [login password]
  (let [salt (crypt/gen-salt)
        password-hash (crypt/encrypt salt password)]
    (insert! :users {:_id (new-id "users")
                     :login login
                     :salt salt
                     :password-hash password-hash
                     :created-at (java.util.Date.)})))

(defn authenticate-user [login password]
  (when-let [{:keys [salt password-hash] :as user}
           (fetch-one :users :where {:login login})]
    (when (= password-hash (crypt/encrypt salt password))
      user)))

(defn login [login password]
  (if-let [user (authenticate-user login password)]
    (session/put! :user user)
    (vali/set-error :login "Invalid username or password")))

(defn logout []
  (session/remove! :user))

(defn logged-in? []
  (session/get :user))
