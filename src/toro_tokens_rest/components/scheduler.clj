(ns toro-tokens-rest.components.scheduler
  (:require [com.stuartsierra.component :as component]))

(defrecord Scheduler [database]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [this]
    (println "Starting Scheduler component...")
    this)

  (stop [this]
    (println "Stopping Scheduler component...")
    this))

(defn new-scheduler []
  (map->Scheduler {}))