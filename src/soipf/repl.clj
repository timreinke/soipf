(ns soipf.repl
  (:use clojure.repl
        somnium.congomongo)
  (:require soipf.db))

(set-connection! (soipf.db/get-mongo-connection))
