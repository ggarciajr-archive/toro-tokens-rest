(ns toro-tokens-rest.components.database
  (:require [clj-leveldb :as l]
            [byte-streams :as bs]
            [com.stuartsierra.component :as component]))

(defrecord Database [path]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [this]
    (println "Starting Database component...")
    (assoc this
      :path path
      :db (l/create-db
            path
            {:key-decoder bs/to-string
             :val-decoder bs/to-string})))

  (stop [this]
    (println "Stopping Database component...")
    (assoc this
      :db nil
      :path nil)))

(defn new-database [path]
  (map->Database {:path path}))