(ns atmos-kernel.web.security.auth
  (:require [buddy.auth.backends :as backends]
            [atmos-kernel.web.core :refer [atmos-response]]
            [cemerick.url :refer [url]]
            [clj-http.client :as http]
            [clojure.data.json :as json]))

(def default-unauthorized-data {:message "Unauthorized"
                                :status  401})

; TODO: Change to defmethod and create different defmulti
(defn get-token
  [entity & {:keys [tokens-provider-uri]
             :or   {tokens-provider-uri (System/getenv "ATMOS_KERNEL_TOKENS_PROVIDER_URI")}}]
  (if-let [request-url (url tokens-provider-uri (name entity))]
    (if-let [response (http/get request-url)]
      (let [response-body (:body response)
            data (json/read-str response-body :key-fn keyword)]
        data))))


(defmacro defauthtokenfn
  [entity & {:keys [tokens-provider-fn]
             :or   {tokens-provider-fn get-token}}]
  `(defn ~'atmos-default-auth-fn
     ~'[request token]
     (try
       (if-let [entity-token# (~tokens-provider-fn ~entity)]
         (if (= ~'token entity-token#) ~entity))
       (catch Exception e# false))))


(defmacro defunauthorizedfn
  [data]
  `(defn ~'atmos-default-unauthorized-fn
     ~'[request metadata]
     (-> (atmos-response (:message ~data))
         (assoc :status (:status ~data)))))


(defn atmos-auth-backend
  [auth-backend data]
  (auth-backend data))


(defn default-token-auth-backend
  [entity]
  (atmos-auth-backend backends/token {:authfn               (defauthtokenfn entity)
                                      :unauthorized-handler (defunauthorizedfn default-unauthorized-data)}))

