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

(defn wrap-params
  [params el]
  )

(defmacro deflens
  [cname nspace & body]
  (let []
    `(defn ~cname 
       [alias# params#]
       )))

(macroexpand '(defalias paths :services
   :cat (sel-cat-id)
   :serv (sel-cat-id :services sel-serv-id)
   :services (sel-cat-id :services)))

