
(ns ssapp.core
  (:require [cljs.nodejs :as nodejs]
            [clojure.core.async :as async]
            [familiar.core :as f :refer [fmtstr]]
            [taoensso.timbre :as log]
            [ssapp.core.macros :refer-macros [async-try <?]]))

(defn ^{:private true} async:aws-call
  [f]
  (let [chan (async/chan 1)]
    (-> (f)
        (.on "success"
             #(async/go
                (log/info "AWS call success")
                (let [val {:data (js->clj (.-data %) :keywordize-keys true)}]
                  (async/>! chan val))))
        (.on "error"
             #(async/go
                (log/info "AWS call error")
                (let [val {:error {:code (.-code %) :message (.-message %)}}]
                  (async/>! chan val))))
        (.send))
    chan))

(defprotocol ^{:private true} AwsS3
  [get-object [this params]]
  [put-object [this params]])

(def ^{:private true :dynamic true} S3
  ;; Declared dynamic only to facilitate testing
  (let [client (new (aget (nodejs/require "aws-sdk") "S3"))]
    (reify AwsS3
      (get-object [this params]
        (log/info (fmtstr "S3 get: ~s" params))
        (async:aws-call #(.getObject client params)))
      (put-object [this params]
        (log/info (fmtstr "S3 put: ~s" params))
        (async:aws-call #(.putObject client params))))))

(defn ^{:private true} async:main
  [chan]
  (async-try
   (let [state (<? chan)]
     (merge state {:x 0}))))
