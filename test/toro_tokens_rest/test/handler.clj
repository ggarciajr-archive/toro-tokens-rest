(ns toro-tokens-rest.test.handler
  (:require [clojure.test :refer :all]
            [toro-tokens-rest.handler :refer :all]
            [ring.mock.request :as mock]))

(deftest test-app
  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
