(defproject atmos-kernel "0.6.0-SNAPSHOT"
  :description "Core of Atmos System"
  :url "https://github.com/AtmosSystem/Kernel"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cloverage "1.0.10"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 ;configuration-deps
                 [aero "1.1.3"]
                 ;web-service-deps
                 [compojure "1.6.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring-cors "0.1.12"]
                 ;logs-deps
                 [ch.qos.logback/logback-classic "1.2.3"]]
  :repositories [["releases" {:url           "https://clojars.org/repo"
                              :username      :env/CLOJAR_USERNAME
                              :password      :env/CLOJAR_PASSWORD
                              :sign-releases false}]])
