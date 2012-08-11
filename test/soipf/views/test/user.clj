(ns soipf.views.test.user
  (:use soipf.test.fixtures
        soipf.models.user
        soipf.models.invitation
        clojure.test
        noir.util.test2
        somnium.congomongo
        clojure.pprint))

(use-fixtures :once (compose-fixtures mongo-connection invitations))

(use-fixtures :each reset-session)

(def ^{:private true} registration-data
  {:login "unused"
   :password "samep"
   :password-confirm "samep"})

(deftest test-invitations
  (testing "display valid invitation"
    (-> (send-request [:get "/register/unused"])
        (has-status 200)
        (body-contains #"Registration")
        (!body-contains #"Invitation not found")))

  (testing "use valid invitation"
    (-> (send-request [:post "/register/unused"]
                      registration-data)
        (has-status 302))
    (is (invitation-consumed? "unused") "Invitation was not consumed")
    (is (= (:login (invitation-used-by "unused")) (:login registration-data))))

  (testing "invalid invitation"
    (-> (send-request [:get "/register/used"])
        (has-status 200)
        (body-contains #"Invitation not found"))
    (-> (send-request [:post "/register/used"]
                      registration-data)
        (has-status 200)
        (body-contains #"Invitation not found"))))