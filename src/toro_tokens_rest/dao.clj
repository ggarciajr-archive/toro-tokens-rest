(ns toro-tokens-rest.dao
  (:require [clj-leveldb :as l]
             [clj-time.format :as f]))

(defn- dt-fmter
  "Generates a formatter to be used by the parse/unparse functions.
  The date-time will be formatted to the following pattern
  yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
  [] (f/formatters :date-time))

(defn- dt-parser
  "Parse a string and return a org.joda.time.DateTime instance."
  [dt]
  (f/parse (dt-fmter) dt))

(defn- dt-unparser
  "Returns the string representation,
  according to the defined formatter, of a given
  org.joda.time.DateTime instance."
  [dt]
  (f/unparse (dt-fmter) dt))

(defn- tkn-serializer
  "Returns a string representation of the token 'object'."
  [tkn]
  (pr-str {:token-id (:token-id tkn)
           :expiration-date (dt-unparser (:expiration-date tkn))
           :remaining-usages (:remaining-usages tkn)}))

(defn- tkn-deserializer
  "Uses a string representation of a token to create a token 'object'."
  [tkn]
  (let [new-tkn (read-string tkn)]
    {:token-id (:token-id new-tkn)
     :expiration-date (dt-parser (:expiration-date new-tkn))
     :remaining-usages (:remaining-usages new-tkn)}))

(defn save!
  "Save the string representation of a token in LevelDB."
  [database token]
  (l/put (:db database)
         (:token-id token)
         (tkn-serializer token)))

(defn get!
  "Look up LevelDB for the given token key.
  Returns token if it finds the token, nil otherwise."
  [database token]
  (let [fnd-tkn (l/get (:db database) token)]
    (if (nil? fnd-tkn)
      nil
      (tkn-deserializer fnd-tkn))))

(defn del!
  "Deletes a token from the LevelDB"
  [database token]
  (l/delete (:db database) (:token-id token)))

(defn find-by
  "Look up LevelDB for tokens matching the given predicate."
  [database predicate]
  (filter predicate (map #(tkn-deserializer (nth % 1)) (lazy-seq (l/iterator (:db database))))))

(defn update!
  "Insert or update tokens data in LevelDB. Saves the token if the token is not
  in LevelDB, update otherwise."
  [database token]
  (if (nil? (get (:db database) (:token-id token)))
    (save! database token)
    (do
      (del! database token)
      (save! database token))))
