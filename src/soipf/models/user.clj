(ns soipf.models.user
  (:require [noir.util.crypt :as crypt]
            [noir.session :as session]
            [noir.validation :as vali]
            [clj-time.core :as time])
  (:use [soipf.util :only [defquery]]
        [somnium.congomongo :only [fetch-one insert!]]))

(defquery create-user [login password]
  (if (fetch-one :users :where {:login login})
    (vali/set-error :login "Username already exists")
    (let [salt (crypt/gen-salt)
          password-hash (crypt/encrypt salt password)]
      (insert! :users {:login login
                       :salt salt
                       :password-hash password-hash
                       :created-at (time/now)}))))

(defquery validate-user [login password]
  (if-let [{:keys [salt password-hash] :as user}
             (fetch-one :users :where {:login login})]
    (if (= password-hash (crypt/encrypt salt password))
      user)))

(defn login [login password]
  (if-let [user (validate-user login password)]
    (session/put! :login (:login user))
    (vali/set-error :login "Invalid username or password")))

(defn logout []
  (session/remove! :login))

(defn logged-in? []
  (session/get :login))
