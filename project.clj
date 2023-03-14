(defproject clj-web-commons "0.1.0"
  :description "lib w/ common tools for web development on clojure"
  :url "https://github.com/lucasarthur/clj-web-commons"
  :license {:name "GNU General Public License v3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.pt-br.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [metosin/ring-swagger "0.26.2"]
                 [metosin/ring-swagger-ui "5.0.0-alpha.0"]
                 [manifold "0.3.0" :exclusions [org.clj-commons/dirigiste]]
                 [aleph "0.6.1"]
                 [ring/ring-defaults "0.3.4"]
                 [compojure "1.7.0"]
                 [cheshire "5.11.0"]
                 [com.appsflyer/ketu "1.0.0" :exclusions [org.slf4j/slf4j-api]]
                 [com.brunobonacci/mulog "0.9.0"]
                 [com.brunobonacci/mulog-kafka "0.9.0"]
                 [nonseldiha/slf4j-mulog "0.2.1"]
                 [environ "1.2.0"]
                 [com.github.seancorfield/next.jdbc "1.3.858"]
                 [com.github.seancorfield/honeysql "2.4.1002"]
                 [lobos "1.0.0-beta3"]
                 [clj-commons/iapetos "0.1.13"]
                 [io.prometheus/simpleclient_hotspot "0.16.0"]])
