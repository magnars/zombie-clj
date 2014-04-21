(defproject zombieclj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [http-kit "2.1.16"]
                 [compojure "1.1.6"]
                 [quiescent "0.1.1"]
                 [jarohen/chord "0.3.1"]]
  :main zombieclj.web
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-cljsbuild "1.0.2"]]
                   :cljsbuild {:builds [{:source-paths ["src"]
                                         :compiler {:output-to "target/classes/public/app.js"
                                                    :optimizations :whitespace
                                                    :pretty-print true}}]}}})
