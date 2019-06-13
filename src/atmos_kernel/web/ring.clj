(ns atmos-kernel.web.ring
  (:require [atmos-kernel.web.security.auth :refer [atmos-auth-backend]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [ring.middleware.defaults :refer [wrap-defaults]]))



(defn def-json-web-api*
  "Create a JSON Web API"
  [routes options auth-backend]
  (-> routes
      (wrap-authentication (atmos-auth-backend auth-backend))
      (wrap-authorization (atmos-auth-backend auth-backend))
      wrap-json-params
      wrap-json-response
      (wrap-defaults options)))

(defmacro def-json-web-api
  ([name routes options]
   `(def ~name (def-json-web-api* ~routes ~options nil)))
  ([name routes options auth-backend]
   `(def ~name (def-json-web-api* ~routes ~options ~auth-backend))))
