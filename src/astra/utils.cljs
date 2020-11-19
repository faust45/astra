(ns astra.utils
  (:require
   [clojure.string :as s]
   [clojure.walk :as wa]))
   
(defn join
  [& args]
  (s/join " " args))


#_(defn assoc-state
  [path value]
  (swap! *state assoc-in path value))

(def p partial)
(defn log-pr
  [& args]
  (apply (.-log js/console) args))

(defn log
  [& args]
  (apply (.-log js/console) (map pr-str args)))

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))
