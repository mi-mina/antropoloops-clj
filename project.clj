(defproject antropoloops-clj "0.1.0-SNAPSHOT"
  :description "antropoloops MAP in clojure"
  :url "www.antropoloops.com" ;¿Qué url es la que se pone aquí?
  :license {:name "Eclipse Public License" ;¿QUé licencia usar?
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [quil "2.4.0-osc"]
                 [de.sojamo/oscp5 "0.9.8"]
                 [org.clojure/data.json "0.2.6"]
                 ]
  :aot [aloops.main]
  :main aloops.main)
