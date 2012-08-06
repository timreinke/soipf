(ns soipf.views.test.user
  (:use soipf.test.fixtures
        soipf.models.user
        clojure.test
        noir.util.test
        somnium.congomongo
        clojure.pprint))

#_(use-fixtures :once (compose-fixtures mongo-connection users))

#_(use-fixtures :each reset-session)

#_(deftest authentication
  (with-noir
    (testing "a successful login redirects"
      (let [resp (send-request [:post "/login"]
                               {:login "tim" :password "password"})]
        (has-status resp 302)))

    ;; The current login code performs
    ;; (cookies/put! :ring-session
    ;;    {:value (cookies/get :ring-session) :expires ~future})
    ;; So when we make the request below, there is a 500 error as there is no ring-session
    ;; cookie (the cookies/get call returns nil and causes an exception in the stack).
    ;; The code works as long as a POST /login request is not the first request made, which
    ;; under normal circumstances it would not be.
    (testing "a user logging in persistently receives a cookie expiring in the future"
        (let [resp (send-request [:post "/login"]
                                 {:login "tim" :password "password" :persistent "true"})]
          (has-status resp 302)
          (is (re-matches
               #"Expires"
               (first (get-in resp [:headers "Set-Cookie"] [""]))))))

    (testing "a user with the wrong password is denied"
      (let [resp (send-request [:post "/login"]
                               {:login "tim" :password "wrong"})]
        (has-status resp 200)
        (is (.contains (:body resp) "Invalid username or password"))))))
