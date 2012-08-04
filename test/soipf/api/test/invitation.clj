(ns soipf.api.test.invitation
  (:use clojure.test
        midje.sweet
        soipf.api.invitation))

(facts "about invitations"
  (invitation {:method :post :key ...key...}) =>
      (contains {:status })
    (provided (generate-invitation-code) => "code"))
