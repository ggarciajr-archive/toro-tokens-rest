(ns toro-tokens-rest.handler
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [toro-tokens-rest.tokens :as tkns]))

(defn get-token [token {body :body}]
  (if (tkns/valid? token)
    {:status 200
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body (tkns/create-token 1)}
    {:status 404
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body {:error "Invalid Token"}}))

(defn increment-usage-time [token]
  (str "incremented token " token))

(defroutes app-routes
           (context "/api/:version/tokens" [api-version]
                    (defroutes tokens-routes
                               (POST "/" [] (tkns/create-token 1))
                               (context "/:token" [token] (defroutes token-routes
                                                                     (GET "/" {body :body} (get-token token body))
                                                                     (POST "/" [] (increment-usage-time token))))))
           (route/not-found "Not Found"))


(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))
