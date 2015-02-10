(ns toro-tokens-rest.tokens
  (:import (java.util UUID))
  (require [clojure.string :as clj-str]
           [clj-time.core :as t]
           [toro-tokens-rest.dao :as dao]))


;;generates a random token, stores it in leveldb, and returns it
(defn create-token
  "Creates a 32 chars long token, from Java's UUID tool,
  and saves the token in the database.
  Each token has an expiration date and
  a counter of how many times it can be used."
  ([database times-to-use hours]
    (let [token
          {:token-id         (clj-str/replace (str (UUID/randomUUID)) "-" "")
           :remaining-usages times-to-use
           :expiration-date  (t/plus (t/now) (t/hours hours))}]
      (dao/save! database token)
      token))
  ([database times-to-use]
    (create-token database times-to-use 24))
  ([database]
    (create-token database 1 24)))


(defn get-token [database token-id]
   "Looks up a token with a given token-id. If the token exists, it returns it. Else, it returns nill."
   (dao/get! database token-id))

(defn valid?
  "A token is considered valid if :remaining-usages > 0 and
  :expiration-date > now."
  [token]
  (true? (and (> (:remaining-usages token) 0)
              (t/after? (:expiration-date token) (t/now)))))

(defn use!
  "Subtracts 1 from token's :remaining-usages, updates it in the database, and returns it"
  [database token]
  (let [new-token (assoc token :remaining-usages (- (:remaining-usages token) 1))]
    (dao/update! database new-token)
    new-token))
