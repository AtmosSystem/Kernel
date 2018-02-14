(ns atmos-kernel.core
  (:require [clojure.string :refer [lower-case capitalize join]]
            [compojure.core :refer [GET]]
            [compojure.route :as route]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]))

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
  (-> routes wrap-json-response wrap-json-body))

(defn ms-atmos-main-method-response
  [ms-name]
  (let [ms-name (-> ms-name name lower-case)]
    (GET "/" [] (str "Welcome to atmos " ms-name " micro-service"))))

(defn ms-atmos-response
  [data]
  (response data))

;-------------------------------------------------------
; END MICRO SERVICE FUNCTIONS
;-------------------------------------------------------


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


