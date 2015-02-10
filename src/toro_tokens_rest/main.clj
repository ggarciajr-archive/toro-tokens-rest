(ns toro-tokens-rest.main
  (:require [com.stuartsierra.component :as component]
            [environ.core :as environ]
            [toro-tokens-rest.components.database :as db]
            [toro-tokens-rest.components.scheduler :as sch]
            [toro-tokens-rest.components.ring :as rng]
            [toro-tokens-rest.components.app :as app]))

(defonce app
  (component/system-map
    :database (db/new-database (environ/env :database-path))
    :scheduler (component/using (sch/new-scheduler) [:database])
    :ring (component/using (rng/new-ring (environ/env :jetty-port)) [:database])
    :app (component/using
           (app/new-app)
           [:database :scheduler :ring])))

(defn -main [& args]
  (alter-var-root #'app component/start))