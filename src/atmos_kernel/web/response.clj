(ns atmos-kernel.web.response
  (:require [ring.util.response :refer [response]]))

(defprotocol ExceptionHandlerProtocol
  (handle-exception [exception request]))

(defn bad-request
  [body]
  {:status  400
   :headers {}
   :body    body})

(defn server-error-request
  [body]
  {:status  500
   :headers {}
   :body    body})

(def responses {:ok           response
                :bad-request  bad-request
                :server-error server-error-request})

(defn atmos-response
  "Create a atmos response"
  ([data response-type]
   (let [response-fn (responses response-type)]
     (response-fn data)))
  ([data]
   (atmos-response data :ok)))