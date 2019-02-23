
(ns ssapp.aws.lambda
  (:require [clojure.core.async :as async]
            [clojure.core.async.impl.protocols :as async-protocol]
            [familiar.core :as f :refer [fmtstr]]
            [taoensso.timbre :as log]
            [ssapp.core :refer [async:main]]))

(defn ^{:private true} pack-output
  [x]
  (clj->js x))

(defn ^{:private true} unpack-input
  [x]
  (js->clj x :keywordize-keys true))

(defn ^{:private true} async:respond
  [x success-p callback]
  (async/go
    (let [x        (if (satisfies? async-protocol/ReadPort x) (async/<! x) x)
          success? (success-p x)
          output   (pack-output x)]
      (if success?
        (do
          (log/info (fmtstr "success=~s" output))
          (callback nil output))
        (do
          (log/error (fmtstr "failure=~a" output))
          (callback output nil)))
      x)))

(defn ^{:export true} handler
  [event context callback]
  (let [[event context] (map unpack-input [event context])]
    (log/info (fmtstr "event=~s, context=~s" event context))
    (-> (async/go event)
        (async:main)
        (async:respond identity callback))))

(set! *main-cli-fn* identity)
