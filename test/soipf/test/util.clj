(ns soipf.test.util
  (:require [midje.sweet :refer :all]
            [soipf.util :refer :all]))

(facts "about namespaced-keyword"
  (fact "it composes a namespace specified as a keyword"
    (fact "with a plain keyword"
      (namespaced-keyword :db :part) => :db/part
      (namespaced-keyword :db.part :user) => :db.part/user)
    (future-fact "with a namespaced keyword? and others"
      (namespaced-keyword :db :part/user :db.part/user)
      (namespaced-keyword :db :part.user :db/part.user))))
