(ns toro-tokens-rest.dao
  (:require [clj-leveldb :as l]
            [byte-streams :as bs]
            [clj-time.format :as f]
            [environ.core :as environ]))

(def  db (l/create-db
          (environ/env :database-path)
          {:key-decoder bs/to-string
           :val-decoder bs/to-string}))

(defn ^:private dt-fmter
  "Generates a formatter to be used by the parse/unparse functions.
  The date-time will be formatted to the following pattern
  yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
  [] (f/formatters :date-time))

(defn ^:private dt-parser
  "Parse a string and return a org.joda.time.DateTime instance."
  [dt]
  (f/parse (dt-fmter) dt))

(defn ^:private dt-unparser
  "Returns the string representation,
  according to the defined formatter, of a given
  org.joda.time.DateTime instance."
  [dt]
  (f/unparse (dt-fmter) dt))

(defn ^:private tkn-serializer
  "Returns a string representation of the token 'object'."
  [tkn]
  (pr-str {:token (:token tkn)
           :expiration-date (dt-unparser (:expiration-date tkn))
           :remaining-usages (:remaining-usages tkn)}))

(defn ^:private tkn-deserializer
  "Uses a string representation of a token to create a token 'object'."
  [tkn]
  (let [new-tkn (read-string tkn)]
    {:token (:token new-tkn)
     :expiration-date (dt-parser (:expiration-date new-tkn))
     :remaining-usages (:remaining-usages new-tkn)}))

(defn save!
  "Save the string representation of a token in LevelDB."
  [token]
  (l/put db
         (:token token)
         (tkn-serializer token)))

(defn get!
  "Look up LevelDB for the given token key.
  Returns token if it finds the token, nil otherwise."
  [token]
  (let [fnd-tkn (l/get db token)]
    (if (nil? fnd-tkn)
      nil
      (tkn-deserializer fnd-tkn))))

(defn del!
  "Deletes a token from the LevelDB"
  [token]
  (l/delete db (:token token)))

(defn find-by
  "Look up LevelDB for tokens matching the given predicate."
  [predicate]
  (filter predicate (map #(tkn-deserializer (nth % 1)) (lazy-seq (l/iterator db)))))

(defn update!
  "Insert or update tokens data in LevelDB. Saves the token if the token is not
  in LevelDB, update otherwise."
  [token]
  (if (nil? (get db (:token token)))
    (save! token)
    (do
      (del! token)
      (save! token))))