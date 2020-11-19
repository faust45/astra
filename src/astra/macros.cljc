(ns astra.macros 
  (:refer-clojure :exclude [slurp])
  (:require
   #?(:cljs
      [astra.utils :refer [log log-pr]])
   [clojure.edn :as edn]
   [clojure.string :as s]
   [cljs.core :as cl]
   [clojure.core]
   [clojure.walk :as walk]))

(defmacro defa
  [name value]
  `(def ~name (atom ~value)))

(defmacro log-time
  [label & body]
  `(do
     (.time js/console ~label)
     ~@body
     (.timeEnd js/console ~label)))
