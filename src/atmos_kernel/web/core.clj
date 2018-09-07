(ns atmos-kernel.web.core
  (:require [atmos-kernel.core :refer [keyword-map]]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]
            [atmos-kernel.web.security.auth :refer [atmos-auth-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(defn- wrap-cors-response
  "Wrap routes in CORS"
  [routes]
  (wrap-cors routes
             :access-control-allow-origin [#".*"]
             :access-control-allow-methods [:get :put :post :delete]))

(defn request-body
  "Get body of compojure request"
  [request]
  (let [body (:body request)] (keyword-map body)))

(defn atmos-response
  "Create a ring response"
  [data]
  (response data))

(defn json-web-app
  "Create a json web application"
  ([routes]
   (json-web-app routes nil))
  ([routes auth-backend]
   (-> routes
       (wrap-authentication (atmos-auth-backend auth-backend))
       (wrap-authorization (atmos-auth-backend auth-backend))
       wrap-cors-response
       wrap-json-response
       wrap-json-body)))