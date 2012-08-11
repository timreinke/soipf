(defproject soipf "0.0.1-SNAPSHOT"
            :description "A simple forum application"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [org.clojure/tools.logging "0.2.4"]
                           [noir "1.3.0-beta8"]
                           [ring-anti-forgery "0.1.1"]
                           [congomongo "0.1.7"]
                           [clj-time "0.3.4"]
                           [org.pegdown/pegdown "1.1.0"]
                           [midje "1.3.1"]
                           [noir-test2 "1.0.0-SNAPSHOT"]
                           [log4j/log4j "1.2.17"]]
            :main soipf.server
            :uberjar-exclusions [#"META-INF/ECLIPSEF.SF"])
