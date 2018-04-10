(ns atmos-kernel.web.route
  (:require [clojure.string :refer [lower-case join]]
            [atmos-kernel.web.core :refer [atmos-response]]
            [compojure.core :refer [GET]]
            [compojure.route :refer [not-found]]))


(def not-found-route (-> {} not-found atmos-response))
(def not-implemented-route (let [data {:message "Not implemented method"}] (-> data not-found atmos-response)))

(defmacro atmos-route
  "Create an atmos compojure route"
  [http-method ms-name route-path & body]
  (let [ms-name (if-not (empty? ms-name) (-> ms-name name lower-case))
        route-params (vec (map #(symbol (name %)) (filter keyword? route-path)))
        route-path (let [route-path (join "/" route-path)]
                     (str "/" ms-name (if-not (empty? route-path)
                                        (str "/" route-path))))
        route-params (if (seq route-params) route-params 'request)]
    `(~http-method ~route-path ~route-params ~@body)))

(defn atmos-main-route
  "Create the main route of web compojure application"
  ([ms-name system]
   (let [ms-name (-> ms-name name lower-case)]
     (atmos-route GET "" []
                  (str "Welcome to " system " " ms-name " micro-service"))))
  ([ms-name]
   (atmos-main-route ms-name "atmos")))