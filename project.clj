(defproject toro-tokens-rest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [ring/ring-json "0.3.1"]
                 [cheshire "5.3.1"]
                 [clj-time "0.9.0-beta1"]
                 [factual/clj-leveldb "0.1.1"]
                 [byte-streams "0.1.13"]]
  :plugins [[lein-ring "0.8.11"]
            [lein-midje "3.1.3"]]
  :ring {:handler toro-tokens-rest.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [midje "1.6.3"]]}})