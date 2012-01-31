(defproject soipf "0.0.1-SNAPSHOT"
            :description "A simple forum application"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [noir "1.2.2"]
                           [ring-anti-forgery "0.1.1"]
                           [org.clojars.doo/congomongo "0.1.8-SNAPSHOT"]]
            :main soipf.server)
