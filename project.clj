(defproject org.clojars.atmos-system/atmos-kernel "2.2-SNAPSHOT"
  :description "Core of Atmos System"
  :url "https://github.com/AtmosSystem/Kernel"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 ;configuration-deps
                 [aero "1.1.6"]
                 ;pem
                 [xsc/pem-reader "0.1.1"]]
  :deploy-repositories [["clojars" {:url      "https://repo.clojars.org/"
                                    :username :env/clojars_username
                                    :password :env/clojars_password}]])
