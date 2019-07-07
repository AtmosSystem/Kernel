(defproject atmos-kernel "1.0"
  :description "Core of Atmos System"
  :url "https://github.com/AtmosSystem/Kernel"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 ;configuration-deps
                 [aero "1.1.3"]
                 ;web-service-deps
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "3.10.0"]
                 [com.cemerick/url "0.1.1"]
                 [compojure "1.6.1"]
                 ;ring
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-codec "1.1.2"]
                 [ring-cors "0.1.12"]
                 ;authorization-deps
                 [buddy/buddy-auth "2.2.0"]
                 ;logs-deps
                 [org.clojure/tools.logging "0.4.1"]
                 [ch.qos.logback/logback-classic "1.2.3"]]
  :repositories [["releases" {:url           "https://clojars.org/repo"
                              :username      :env/CLOJAR_USERNAME
                              :password      :env/CLOJAR_PASSWORD
                              :sign-releases false}]])
