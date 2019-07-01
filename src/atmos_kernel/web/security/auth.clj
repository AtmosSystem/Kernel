(ns atmos-kernel.web.security.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [atmos-kernel.configuration :refer [read-edn]]
            [atmos-kernel.core :refer [log-data]]))

(def basic-auth backends/basic)
(def session-auth backends/session)
(def token-auth backends/token)
(def jws-auth backends/jws)
(def jwe-auth backends/jwe)

(def cors-config (read-edn :cors))

(defmulti cors-access-control (fn [type-to-allow] type-to-allow))

(defmethod cors-access-control :origins [_] (map #(re-pattern %) (cors-config :origins)))
(defmethod cors-access-control :methods [_] (cors-config :methods))

(defprotocol AuthHandlerProtocol
  (get-authentication [request auth-data]))

(defn- auth-backend-fn
  [request auth-data]
  (get-authentication request auth-data))

(defn atmos-auth-backend
  [auth-backend]
  (if auth-backend
    (let [auth-backend-data {:realm      "ATMOS"
                             :token-name "Bearer"
                             :authfn     auth-backend-fn}]
      (auth-backend auth-backend-data))))


(defmacro handle-request
  [request authentication-needed? body]
  `(log-data :atmos-kernel :debug {:request                ~request
                                   :authentication-needed? ~authentication-needed?}
             (if ~authentication-needed?
               (if-not (authenticated? ~request)
                 (throw-unauthorized)
                 ~body)
               ~body)))