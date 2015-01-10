(ns toro-tokens-rest.test.dao
  (:use [midje.sweet])
  (:require [toro-tokens-rest.tokens :as tkns]
            [toro-tokens-rest.dao :as tkns-dao]))

(fact "Retrieved token must be equal to original token"
      (let [token (tkns/create-token)
            svd-tkn (tkns-dao/save! token)
            fnd-tkn (tkns-dao/get! (:token token))]
        (= token fnd-tkn) => true))

(fact "Deleted tokens must not be found"
      (let [token (tkns/create-token)]
        (do
          (tkns-dao/save! token)
          (tkns-dao/del! token))
        (nil? (tkns-dao/get! (:token token))) => true))

(fact "Find by predicate must return only tokens matching the given predicate."
      (let [tokens (map tkns/create-token (take 2 (repeat 1)))
            fst-tkn (first tokens)]
        (map #(tkns-dao/save! %) tokens)
        (tkns-dao/update! (tkns/use! fst-tkn))
        (empty? (filter #(= fst-tkn %) (tkns-dao/find-by tkns/valid?))) => true))