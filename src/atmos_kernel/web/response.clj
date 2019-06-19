(ns atmos-kernel.web.response
  (:require [ring.util.response :refer [response bad-request]]))

(def responses {:ok          response
                :bad-request bad-request})

(defn atmos-response
  "Create a atmos response"
  ([data response-type]
   (let [response-fn (responses response-type)]
     (response-fn data)))
  ([data]
   (atmos-response data :ok)))