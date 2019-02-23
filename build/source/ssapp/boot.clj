
(ns ssapp.boot
  (:refer-clojure :exclude [test])
  (:require [boot.core                   :as core]
            [boot.task.built-in          :as task]
            [adzerk.boot-cljs            :as cljs       :refer [cljs]]
            [boot-aws-lambda-kit.core    :as lambda-kit :refer [handler:cljs]]
            [cider.piggieback            :as piggieback :refer [cljs-repl]]
            [cljs.repl.node              :as cljs-node  :refer [repl-env]]
            [crisptrutski.boot-cljs-test :as cljs-test  :refer [test-cljs]]
            [degree9.boot-npm]))

(core/deftask npm
  []
  (degree9.boot-npm/npm :install {:aws-sdk "2.325.0"}))

(core/deftask dev
  []
  (comp (npm)
        (cljs :ids #{"ssapp"} :optimizations :none)
        (task/target :dir #{"out"})))

(defn start-cljs-repl
  "Entry point for REPL-based development."
  []
  @(future (core/boot (dev)))
  (piggieback/cljs-repl (cljs.repl.node/repl-env :path ["out/node_modules"])))

(core/deftask autotest
  []
  (core/task-options! test-cljs {:js-env :node})
  (comp (npm)
        (task/watch)
        (test-cljs)))

(core/deftask test
  []
  (core/task-options! test-cljs {:exit? true :js-env :node})
  (comp (npm)
        (test-cljs)))

(core/deftask build
  []
  (comp (npm)
        (cljs :ids #{"ssapp"}
              :optimizations :none
              :compiler-options {:target :nodejs})
        (handler:cljs :ids  #{"ssapp"})
        (task/target :dir #{"target"})))
