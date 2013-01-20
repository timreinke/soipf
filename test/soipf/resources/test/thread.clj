(ns soipf.models.test.thread
  (:use clojure.test
        soipf.models.thread
        soipf.test.fixtures
        somnium.congomongo))

(use-fixtures :once mongo-connection)
(use-fixtures :each (mongo-cleanup-collections "threads"))

#_(deftest test-create-thread!
  (testing "")
  (let [params {:title "Foo" :body "Bar"}]
    (is )))