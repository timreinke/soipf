(defproject soipf "0.0.1-SNAPSHOT"
            :description "A simple forum application"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [org.clojure/tools.logging "0.2.4"]
                           [compojure "1.1.5"]
                           [ring-anti-forgery "0.1.1"]
                           [clj-time "0.3.4"]
                           [com.datomic/datomic-free "0.8.3767"]
                           [org.pegdown/pegdown "1.1.0"]
                           [log4j/log4j "1.2.17"]]
            :plugins [[lein-ring "0.8.0"]]
            :profiles {:dev
                       {:dependencies
                        [[midje "1.4.0" :exclusions [org.clojure/clojure]]
                         [ring-serve "0.1.2"]
                         [ring-server "0.2.7"]
                         [ring-refresh "0.1.2"]]
                        :plugins
                        [[lein-midje "2.0.4"]]}}
            :main soipf.server
            :checksum :warn
            :uberjar-exclusions [#"META-INF/ECLIPSEF.SF"]

            :ring {:handler soipf.app/handler
                   :init soipf.init/init})
