(ns atmos-kernel.core
  (:require [clojure.string :refer [lower-case capitalize join]]
            [compojure.core :refer [GET]]
            [compojure.route :as route]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [aero.core :refer [read-config]]
            [ring.middleware.cors :refer [wrap-cors]]))

;-------------------------------------------------------
; BEGIN GENERAL FUNCTIONS
;-------------------------------------------------------

(defn keyword-map
  [data]
  (into {} (map (fn [[k v]] [(keyword k) v]) data)))

(defn read-resource-edn
  [file]
  (read-config (str "resources/" (name file) ".edn")))

;-------------------------------------------------------
; END GENERAL FUNCTIONS
;-------------------------------------------------------


;-------------------------------------------------------
; BEGIN MICRO SERVICE FUNCTIONS
;-------------------------------------------------------

(def not-found-route (route/not-found (response {})))
(def not-implemented-fn (route/not-found (response {:message "Not implemented method"})))

(defn ms-atmos-method
  [ms-name & params]
  (let [ms-name (-> ms-name name lower-case)
        params (join "/" params)]
    (str "/"
         ms-name
         (if-not
           (empty? params)
           (str "/" params)))))

(defn make-json-app
  [routes]
  (let [routes (wrap-cors routes
                          :access-control-allow-origin [#".*"]
                          :access-control-allow-methods [:get :put :post :delete])]
    (-> routes wrap-json-response wrap-json-body)))

(defn ms-atmos-main-method-response
  [ms-name]
  (let [ms-name (-> ms-name name lower-case)]
    (GET "/" [] (str "Welcome to atmos " ms-name " micro-service"))))

(defn ms-atmos-response
  [data]
  (response data))

(defn request-body
  [request]
  (let [body (:body request)]
    (keyword-map body)))


(defmacro ms-atmos-cond-response
  [& body]
  `(ms-atmos-response
     (cond
       ~@body)))

(defmacro ms-atmos-let-cond-response
  [bindings & body]
  `(let ~bindings
     (ms-atmos-cond-response ~@body)))

;-------------------------------------------------------
; END MICRO SERVICE FUNCTIONS
;-------------------------------------------------------


(defmacro defatmos-seq-record-protocol
  [record-name record-plural-name type]
  (let [entity-name (name record-name)
        protocol-name# (symbol (str "I" entity-name "Seq" (name type)))
        fn-name# #(list
                    (symbol (str (name %1) "-" (lower-case (name record-plural-name))))
                    '[value])]
    `(defprotocol ~protocol-name#
       ~(fn-name# :get)
       ~(fn-name# :remove))))

(defmacro defatmos-record-protocols
  [record-name type]
  (let [entity-name (name record-name)
        entity-lower-name (lower-case entity-name)
        protocol-name# #(symbol (str "I" entity-name (name %1) (name type)))
        fn-name# #(list
                    (symbol (str (name %1) "-" entity-lower-name))
                    '[value])]
    `[(defprotocol ~(protocol-name# :Basic)
        ~(fn-name# :add)
        ~(fn-name# :update))

      (defprotocol ~(protocol-name# :Identity)
        ~(fn-name# :get)
        ~(fn-name# :remove))]))


