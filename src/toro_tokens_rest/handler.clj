(ns toro-tokens-rest.handler
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [toro-tokens-rest.tokens :as tkns]))

(defn get-token [token]
  (let [lookup (tkns/get-token token)]
  (if lookup
    {:status 200
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body (str lookup)}
    {:status 404
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body {:error "Invalid Token"}})))

(defn create-token [body]  
    {:status 200
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body (str(tkns/create-token 1))})

(defn increment-usage-time [token]
  (str "incremented token " token))

(defroutes app-routes
           (context "/api/:version/tokens" [api-version]
                    (defroutes tokens-routes
                               (POST "/" [] (create-token 1))
                               (context "/:token" [token] (defroutes token-routes
                                                                     (GET "/" [] (get-token token))
                                                                     (POST "/" [] (increment-usage-time token))))))
           (route/not-found "Not Found"))


(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))
