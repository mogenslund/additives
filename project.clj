(defproject mogenslund/additives "0.1.0-SNAPSHOT"
  :description "Addons to Liquid"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta1"]
                 [mogenslund/liquid "0.8.0"]]
  :resource-paths ["resources"]
  :target-path "/tmp/liq/target/%s/"
  :clean-targets ^{:protect false} [:target-path]
  :profiles {:uberjar {:aot :all}})