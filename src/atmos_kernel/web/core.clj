(ns atmos-kernel.web.core
  (:require [atmos-kernel.core :refer [keyword-map]]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defn- wrap-cors-response
  [routes]
  (wrap-cors routes
             :access-control-allow-origin [#".*"]
             :access-control-allow-methods [:get :put :post :delete]))

(defn request-body
  [request]
  (let [body (:body request)] (keyword-map body)))

(defn atmos-response
  [data]
  (response data))

(defn json-web-app
  [routes]
  (-> routes wrap-cors-response wrap-json-response wrap-json-body))