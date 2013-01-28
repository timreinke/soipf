(ns soipf.resources.test.user
  (:require [midje.sweet :refer :all]
            [soipf.resources :refer [get-context authorized-for?]]
            [soipf.db :refer [fetch]]
            [soipf.resources.user :as user :refer [get-user]]))

(facts "about retrieving users"
  (fact "asking for an existing user returns that user"
    (get-user ...login...) => ...user...
    (provided (fetch {user/login ...login...} anything) => ...user...
              (get-context [:session :current-user]) => ...current-user...
              (authorized-for? ...current-user... :get anything) => true))
  (fact "asking for an existing user's hash & salt"
    (fact "succeeds when authorized"
      (get-user ...login... user/auth-view) => ...user...
      (provided (fetch {user/login ...login...} user/auth-view)
                    => ...user...
                (get-context [:session :current-user])
                    => ...current-user...
                (authorized-for? ...current-user... :get user/auth-view)
                    => true))
    (fact "and fails when not authorized"
      (get-user ...login... user/auth-view) => (throws Exception)
      (provided (get-context [:session :current-user])
                    => ...current-user...
                (authorized-for? ...current-user... :get user/auth-view)
                => false))))