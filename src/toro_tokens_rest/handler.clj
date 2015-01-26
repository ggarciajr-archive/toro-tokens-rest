(ns toro-tokens-rest.handler
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [toro-tokens-rest.tokens :as tkns]))

;;temporary API key
(def api-key "123")

(defn valid-api-key? [key]
  (= key api-key))

(defn get-token [token-id]
  (if-let [result (tkns/get-token token-id)]
    {:status 200
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body (str result)}
    {:status 404
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body {:error "Invalid token ID"}}))
    

(defn create-token 
  "Handler for creating a token. Parses the body, passes it off to tkns/create-token, and then forms the response."
  [body]
  (if (valid-api-key? (get body "api-key"))   
    {:status 200
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body (str(tkns/create-token (get body "remaining-usages" 1) (get body "hours-till-expiration" 24) ))}
    {:status 401
     :headers {"Content-Type" "application/json; charset=utf-8"}
     :body {:error "Invalid API key"}}))
        
(defn use-token [body token-id]
  (if (valid-api-key? (get body "api-key"))
    (if-let [result (tkns/get-token token-id)]
      (if (tkns/valid? result)
         {:status 200
          :headers {"Content-Type" "application/json; charset=utf-8"}
          :body (str (tkns/use! result))}
         {:status 405
          :headers {"Content-Type" "application/json; charset=utf-8"}
          :body (str result)})
      
      {:status 404
       :headers {"Content-Type" "application/json; charset=utf-8"}
       :body {:error "Invalid token ID"}}) 
    {:status 401
       :headers {"Content-Type" "application/json; charset=utf-8"}
       :body {:error "Invalid API key"}}))
    

    
(defroutes app-routes
           (context "/api/:version/tokens" [api-version]
                    (defroutes tokens-routes
                               (POST "/" {b :body} (create-token b))
                               (context "/:token-id" [token-id] (defroutes token-routes
                                                                     (GET "/" [] (get-token token-id))
                                                                     (POST "/" {b :body} (use-token b token-id))))))
           (route/not-found "Not Found"))


(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))
