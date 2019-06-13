(ns atmos-kernel.web.response
  (:require [ring.util.response :refer [response]]))


(defn atmos-response
  "Create a atmos response"
  [data]
  (response data))