(ns atmos-kernel.web.security.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))

(def basic-auth backends/basic)
(def session-auth backends/session)
(def token-auth backends/token)
(def jws-auth backends/jws)
(def jwe-auth backends/jwe)

(defprotocol IAuthHandlerProtocol
  (get-authentication [request auth-data]))

(defn atmos-auth-backend
  [auth-backend]
  (if auth-backend
    (auth-backend {:realm  "ATMOS"
                   :authfn (fn [request auth-data]
                             (get-authentication request auth-data))})))


(defmacro handle-request
  [request authentication-needed? body]
  `(if ~authentication-needed?
     (if-not (authenticated? ~request)
       (throw-unauthorized)
       ~body)
     ~body))