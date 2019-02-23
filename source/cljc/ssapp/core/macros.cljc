
(ns ssapp.core.macros)

(defmacro async-try
  [& body]
  `(async/go
     (try
       ~@body
       (catch :default e
         e))))

(defn ^{:private true} throw-err
  [e]
  (when (instance? #?(:clj Exception :cljs js/Error) e)
    (throw e))
  e)

(defmacro <?
  [ch]
  `(throw-err (async/<! ~ch)))
