(ns atmos-kernel.web.ring
  (:require [atmos-kernel.web.security.auth :refer [atmos-auth-backend cors-access-control]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [ring.middleware.defaults :refer [wrap-defaults]]))


(defn wrap-secure-json-web-api
  "Wrap a JSON Web API"
  [routes options auth-backend]
  (-> routes
      (wrap-authentication (atmos-auth-backend auth-backend))
      (wrap-authorization (atmos-auth-backend auth-backend))
      (wrap-cors :access-control-allow-origin (cors-access-control :origins)
                 :access-control-allow-methods (cors-access-control :methods))
      wrap-json-response
      wrap-json-params
      (wrap-defaults options)))

(defmacro def-json-web-api
  ([name routes options]
   `(def ~name (wrap-secure-json-web-api ~routes ~options nil)))
  ([name routes options auth-backend]
   `(def ~name (wrap-secure-json-web-api ~routes ~options ~auth-backend))))
