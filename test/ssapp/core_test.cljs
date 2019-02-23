
(ns ssapp.core-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [cljs.test :as test]
            [clojure.core.async :as async]
            [ssapp.core :as core :refer [async:main]]))

(deftest test:something
  (test/async fin
    (async/go
      (is (- {:x 0} (async/<! (async:main (async/go {})))))
      (fin))))
