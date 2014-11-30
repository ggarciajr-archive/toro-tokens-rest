(ns toro-tokens-rest.handler
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]))

(def test-token {:token "44c2617815014a18976866941d42d0ff"
                 :expiration-date "2014-09-10 23:59:59 GMT-3"
                 :remaining-usages 1})

(defn valid-token? [token]
  (= "44c2617815014a18976866941d42d0ff" token))

(defn get-token [token {body :body}]
  (println (body :api-key))
  (if (valid-token? token)
    {:status 200
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body test-token}
    {:status 404
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body {:error "Invalid Token"}}))

(defn create-token []
  (.replace (str (java.util.UUID/randomUUID)) "-" ""))

(defn increment-usage-time [token]
  (str "incremented token " token))

(defroutes app-routes
           (context "/api/:version/tokens" [api-version]
                    (defroutes tokens-routes
                               (POST "/" [] (create-token))
                               (context "/:token" [token] (defroutes token-routes
                                                                     (GET "/" {body :body} (get-token token body))
                                                                     (POST "/" [] (increment-usage-time token))))))
           (route/not-found "Not Found"))


(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))
