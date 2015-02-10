(ns toro-tokens-rest.routes
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [toro-tokens-rest.tokens :as tkns]))

(defn- response-json [status-number body]
  {:status status-number
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body body})

(defn- get-token [database token-id]
  (if-let [result (tkns/get-token database token-id)]
    (response-json 200 (str result))
    (response-json 404 {:error "Invalid token ID"})))

(defn- create-token
  "Handler for creating a token. Parses the body, passes it off to tkns/create-token, and then forms the response."
  [database body params]
  (response-json 200 (str(tkns/create-token database
                                            (get body "remaining-usages" 1)
                                            (get body "hours-till-expiration" 24)))))

(defn- use-token [database token-id]
  (if-let [result (tkns/get-token database token-id)]
      (if (tkns/valid? result)
         (response-json 200 (str (tkns/use! database result)))
         (response-json 405 (str result)))
      (response-json 404 {:error "Invalid token ID"})))

(defn- toro-routes [database]
  (compojure.core/routes
    (GET "/api/:version/tokens/:token-id" [api-version token-id] (get-token database token-id))
    (POST "/api/:version/tokens/:token-id" [api-version token-id] (use-token database token-id))
    (POST "/api/:version/tokens" {p :params b :body} (create-token database b p))
    (route/not-found "Not Found")))

(defn create-routes [database]
  (-> (handler/api (toro-routes database))
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))