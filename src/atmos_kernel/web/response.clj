(ns atmos-kernel.web.response
  (:require [ring.util.response :refer [response]]))


(defn atmos-bad-request-response
  [data]
  {:status  400
   :headers {}
   :body    data})

(defn atmos-ok-response
  "Create a atmos response"
  [data]
  (response data))