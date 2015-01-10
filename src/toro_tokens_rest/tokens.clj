(ns toro-tokens-rest.tokens
  (:import (java.util UUID))
  (require [clojure.string :as clj-str]
           [clj-time.core :as t]))

(defn create-token
  "Creates a 32 chars long token.
  Each token has an expiration date and
  a counter of how many times it can be used."
  ([times-to-use hours]
    {:token            (clj-str/replace (str (UUID/randomUUID)) "-" "")
     :remaining-usages times-to-use
     :expiration-date  (t/plus (t/now) (t/hours hours))})
  ([times-to-use]
    (create-token times-to-use 24))
  ([]
    (create-token 1 24)))

(defn valid?
  "A token is considered valid if :remaining-usages > 0 and
  :expiration-date > now."
  [token]
  (true? (and (> (:remaining-usages token) 0)
              (t/after? (:expiration-date token) (t/now)))))

(defn use!
  "Subtracts 1 from token's :remaining-usages"
  [token]
  (assoc token :remaining-usages (- (:remaining-usages token) 1)))