(ns soipf.resources.test.user
  (:require [midje.sweet :refer :all]
            [soipf.resources :refer [fetch get-context authorized-for]]
            [soipf.resources.user :refer [get-user]]))

(facts "about retrieving users"
  (fact "asking for an existing user returns that user"
    (get-user ...login...) => ...user...
    (provided (fetch :users ...login...) => ...user...))
  (fact "asking for an existing user's hash & salt"
    (fact "succeeds when authorized"
      (get-user ...login... {:only [:password-hash :salt]}) => ...user...
      (provided (fetch :users ...login... {:only [:password-hash :salt]})
                    => ...user...
                (get-context [:session :current-user]) => ...current-user...
                (authorized-for :data [:user :password-hash]) => true))
    (fact "and fails when not authorized")))