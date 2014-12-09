(ns toro-tokens-rest.tokens
  (:import (java.util UUID))
  (require [clojure.string :as clj-str]
           [clj-time.core :as t]
           [clj-time.format :as f]))

(defn create-token
  "Creates a 32 chars long token.
  Each token has an expiration date and
  a counter of how many times it can be used."
  [times-to-use & hours]
  (let [h (if (nil? hours)
            24
            (first hours))]
    {:token            (clj-str/replace (str (UUID/randomUUID)) "-" "")
     :remaining-usages times-to-use
     :expiration-date  (t/plus (t/now) (t/hours h))}))

(defn valid-token?
  "A token is considered valid if :remaining-usages > 0 and
  :expiration-date > now."
  [token]
  (true? (and (> (:remaining-usages token) 0)
              (t/after? (:expiration-date token) (t/now)))))