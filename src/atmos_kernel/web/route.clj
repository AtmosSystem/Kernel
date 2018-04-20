(ns atmos-kernel.web.route
  (:require [clojure.string :refer [lower-case join]]
            [atmos-kernel.web.core :refer [atmos-response]]
            [compojure.core :refer [GET POST PUT DELETE defroutes]]
            [compojure.route :refer [not-found]]))


(def not-found-route (-> {} not-found atmos-response))
(def not-implemented-route (let [data {:message "Not implemented method"}] (-> data not-found atmos-response)))

(defmacro atmos-route
  "Create an atmos compojure route"
  ([http-method route-path body]
   (let [route-params (vec (map #(symbol (name %)) (filter keyword? route-path)))
         route-path (let [route-path (join "/" route-path)]
                      (str "/" (if-not (empty? route-path) route-path)))
         route-params (if (seq route-params) route-params 'request)]
     `(~http-method ~route-path
        ~route-params
        (atmos-response ~body)))))

(defmacro atmos-GET
  [path body]
  `(atmos-route GET ~path ~body))

(defmacro atmos-POST
  [path body]
  `(atmos-route POST ~path ~body))

(defmacro atmos-PUT
  [path body]
  `(atmos-route PUT ~path ~body))

(defmacro atmos-DELETE
  [path body]
  `(atmos-route DELETE ~path ~body))

(defmacro atmos-main-route
  "Create the main route of web compojure application"
  ([ms-name]
   (let [ms-name (-> ms-name name lower-case)]
     `(atmos-GET [] (str "Welcome to " ~ms-name " micro-service")))))

(defmacro defatmos-route
  "Define atmos route"
  [name ms-name & routes]
  (let [route-name (symbol (str name "-internal"))]
    `(def ~name (defroutes ~route-name
                           (atmos-main-route ~ms-name)
                           ~@routes
                           not-found-route))))