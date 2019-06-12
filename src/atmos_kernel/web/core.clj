(ns atmos-kernel.web.core
  (:require [ring.util.response :refer [response]]))


(defn atmos-response
  "Create a ring response"
  [data]
  (response data))
