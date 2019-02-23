
(set-env!
  :source-paths   #{"build/resource/cljs-config"
                    "build/source"
                    "source/cljc"
                    "source/cljs"
                    "test"})

(set-env!
 :dependencies '[[org.clojure/clojure         "1.10.0"]
                 [org.clojure/clojurescript   "1.10.191"]

                 [org.clojure/core.async      "0.4.490"        :scope "test"]
                 [familiar                    "0.2.0"          :scope "test"]
                 [com.taoensso/timbre         "4.10.0"         :scope "test"]

                 [adzerk/boot-cljs            "2.1.4"          :scope "test"]
                 [boot-aws-lambda-kit         "0.2.0-SNAPSHOT" :scope "test"]
                 [crisptrutski/boot-cljs-test "0.3.4"          :scope "test"]
                 [cider/piggieback            "0.3.10"         :scope "test"]
                 [degree9/boot-npm            "0.2.0"          :scope "test"]
                 [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]])
                              ; needed by piggieback

(require '[ssapp.boot :refer :all])
