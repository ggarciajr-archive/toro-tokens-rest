(ns toro-tokens-rest.components.ring
  (:require [com.stuartsierra.component :as component]
            [toro-tokens-rest.routes :as r]
            [ring.adapter.jetty :as rng-jetty]))


(defrecord Ring [port database jetty-server]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [this]
    (println "Starting Ring component...")
    (let [jetty-server (rng-jetty/run-jetty (r/create-routes database) {:port port :join? false})]
      (assoc this
        :jetty-server jetty-server)))

  (stop [this]
    (println "Stopping Ring component...")
    (.stop jetty-server)
    (assoc this
      :jetty-server nil)))

(defn new-ring [port]
  (map->Ring {:port port}))