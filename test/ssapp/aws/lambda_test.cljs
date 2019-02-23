
(ns ssapp.aws.lambda-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [cljs.test :as test]
            [clojure.core.async :as async]
            [ssapp.aws.lambda :as l :refer [async:respond handler]]))

(def ^{:private true :dynamic true} captured nil)

(defn ^{:private true} capture
  [& args]
  (vreset! captured args))

(deftest test:something
  (test/async fin
    (async/go
      (binding [captured (volatile! nil)]
        (async/<! (async:respond "foo" (constantly true) capture)))
      (is (= [nil "foo"] @captured))
      (fin))))
