(ns toro-tokens-rest.test.tokens
  (:use [midje.sweet])
  (:require [toro-tokens-rest.tokens :as tkns]
            [clj-time.core :as t]))

(fact "token created with default hours should expire in 24 hours and be 32 chars long."
      (let [token (tkns/create-token)
            expiration-date (:expiration-date token)]
        (:remaining-usages token) => 1
        (t/after? expiration-date (t/plus (t/now) (t/hours 23))) => true
        (t/after? expiration-date (t/plus (t/now) (t/hours 25))) => false
        (count (:token token)) => 32))

(fact "token created with custom hours should have custom expire date hours."
      (let [token (tkns/create-token 1 13)
            expiration-date (:expiration-date token)]
        (:remaining-usages token) => 1
        (t/after? expiration-date (t/plus (t/now) (t/hours 12))) => true
        (t/after? expiration-date (t/plus (t/now) (t/hours 14))) => false))

(fact "token is valid if :remaining-usages > 0 and :expiration-date < now"
      (let [valid-token (tkns/create-token 1)
            invalid-token (tkns/create-token 0 1)
            invalid-token-exp-date (:expiration-date invalid-token)
            valid-token-exp-date (:expiration-date valid-token)]
        (tkns/valid? valid-token) => true
        (tkns/valid? invalid-token) => false
        (t/after? valid-token-exp-date (t/plus (t/now) (t/hours 12))) => true
        (t/after? invalid-token-exp-date (t/plus (t/now) (t/hours 12))) => false))

(fact "use token should return a new token with the original token :remaining-usages subtracted by 1."
      (let [valid-token (tkns/create-token 2)]
        (:remaining-usages valid-token) => 2
        (:remaining-usages (tkns/use! valid-token)) => 1))