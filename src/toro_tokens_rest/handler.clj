(ns toro-tokens-rest.handler
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [toro-tokens-rest.tokens :as tkns]))


(defn response-json [status-number body]
  {:status status-number
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body body})

(defn get-token [token-id]
  (if-let [result (tkns/get-token token-id)]
    (response-json 200 (str result))
    (response-json 404 {:error "Invalid token ID"})))
    
(defn create-token 
  "Handler for creating a token. Parses the body, passes it off to tkns/create-token, and then forms the response."
  [body]
  (response-json 200 (str(tkns/create-token (get body "remaining-usages" 1) (get body "hours-till-expiration" 24) ))))
        
(defn use-token [token-id]
  (if-let [result (tkns/get-token token-id)]
      (if (tkns/valid? result)
         (response-json 200 (str (tkns/use! result)))
         (response-json 405 (str result)))
      (response-json 404 {:error "Invalid token ID"})))
    

    
(defroutes app-routes
           (context "/api/:version/tokens" [api-version]
                    (defroutes tokens-routes
                               (POST "/" {b :body} (create-token b))
                               (context "/:token-id" [token-id] (defroutes token-routes
                                                                     (GET "/" [] (get-token token-id))
                                                                     (POST "/" [] (use-token token-id))))))
           (route/not-found "Not Found"))


(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))
