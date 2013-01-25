(ns soipf.resources.test.user
  (:require [midje.sweet :refer :all]
            [soipf.resources :refer [fetch get-context authorized-for?]]
            [soipf.resources.user :refer [get-user auth-view]]))

;.;. A journey of a thousand miles begins with a single step. --
;.;. @alanmstokes
(facts "about retrieving users"
  (fact "asking for an existing user returns that user"
    (get-user ...login...) => ...user...
    (provided (fetch :users ...login... anything) => ...user...
              (get-context [:session :current-user]) => ...current-user...
              (authorized-for? ...current-user... :get anything) => true))
  (fact "asking for an existing user's hash & salt"
    (fact "succeeds when authorized"
      (get-user ...login... {:fields auth-view}) => ...user...
      (provided (fetch :users ...login... {:fields auth-view})
                    => ...user...
                (get-context [:session :current-user])
                    => ...current-user...
                (authorized-for? ...current-user... :get auth-view)
                    => true))
    (fact "and fails when not authorized"
      (get-user ...login... {:fields auth-view}) => (throws Exception)
      (provided (get-context [:session :current-user])
                    => ...current-user...
                (authorized-for? ...current-user... :get auth-view)
                => false))))