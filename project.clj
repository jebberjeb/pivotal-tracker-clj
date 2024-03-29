(defproject pt "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure             "1.5.1"]
                 [org.clojure/tools.logging       "0.2.3"]
                 [org.clojure/tools.cli           "0.2.4"]
                 [clj-time                        "0.6.0"]
                 [prismatic/schema                "0.2.1"]
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [cheshire                        "5.0.1"]
                 [clj-http                        "0.9.0"]
                 [instaparse                      "1.2.16"]
                 [digest                          "1.4.3"]
                 [hiccup                          "1.0.5"]]
  :main pt.cli
  :profiles {:uberjar {:aot :all}})
