(defproject atmos-kernel "2.1-SNAPSHOT"
  :description "Core of Atmos System"
  :url "https://github.com/AtmosSystem/Kernel"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ;configuration-deps
                 [aero "1.1.6"]
                 ;pem
                 [xsc/pem-reader "0.1.1"]]
  :repositories [["releases" {:url           "https://clojars.org/repo"
                              :username      :env/CLOJAR_USERNAME
                              :password      :env/CLOJAR_PASSWORD
                              :sign-releases false}]
                 ["snapshot" {:url           "https://clojars.org/repo"
                              :username      :env/CLOJAR_USERNAME
                              :password      :env/CLOJAR_PASSWORD
                              :sign-releases false}]])
