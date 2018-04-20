(ns atmos-kernel.web.route
  (:require [clojure.string :refer [lower-case join]]
            [atmos-kernel.web.core :refer [atmos-response]]
            [compojure.core :refer [GET POST PUT DELETE]]
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
  ([path body]
   `(atmos-route GET ~path ~body))
  ([body]
   (atmos-GET [] body)))

(defmacro atmos-POST
  ([path body]
   `(atmos-route POST ~path ~body))
  ([body]
   (atmos-POST [] body)))

(defmacro atmos-PUT
  ([path body]
   `(atmos-route PUT ~path ~body))
  ([body]
   (atmos-PUT [] body)))

(defmacro atmos-DELETE
  ([path body]
   `(atmos-route DELETE ~path ~body))
  ([body]
   (atmos-DELETE [] body)))

(defn atmos-main-route
  "Create the main route of web compojure application"
  ([ms-name system]
   (let [ms-name (-> ms-name name lower-case)]
     (atmos-GET (str "Welcome to " system " " ms-name " micro-service"))))
  ([ms-name]
   (atmos-main-route ms-name "atmos")))