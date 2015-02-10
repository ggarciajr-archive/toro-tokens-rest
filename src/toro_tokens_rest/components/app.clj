(ns toro-tokens-rest.components.app
  (:require [com.stuartsierra.component :as component]))


(defrecord App [database scheduler ring]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [this]
    (println "Starting App component...")
    this)

  (stop [this]
    (println "Stopping App component...")
    this))

(defn new-app []
  (map->App {}))