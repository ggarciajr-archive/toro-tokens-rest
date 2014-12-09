(ns toro-tokens-rest.test.tokens
  (:require [clojure.test :refer :all]
            [toro-tokens-rest.tokens :refer :all]
            [clj-time.core :as t]))

(deftest test-tokens
  (testing "token creation with default hours parameter"
    (let [token (create-token 1)]
      (is (= (:remaining-usages token) 1))
      (is (t/after? (:expiration-date token) (t/now)))
      (is (= (count (:token token)) 32))))

  (testing "token creation with custom hours parameter"
    (let [token (create-token 1 12)
          hours (t/plus (t/now) (t/hours 13))]
      (is (= (:remaining-usages token) 1))
      (is (t/after? (:expiration-date token) (t/now)))
      (is (false? (t/after? (:expiration-date token) hours)))
      (is (= (count (:token token)) 32))))

  (testing "token validity"
    (let [valid-token (create-token 1)
          invalid-token (create-token 0)]
      (is (true? (valid-token? valid-token)))
      (is (false? (valid-token? invalid-token))))))
