(ns astra.render 
  (:require-macros
   [astra.macros :refer [log-time]])
  (:require 
   [hipo.core :as hipo]
   [clojure.set :as set]
   [astra.utils :refer [log]]))

(def dirty-components (atom #{}))
(def mark-dirty #(swap! dirty-components conj %))
(def off-dirty  #(swap! dirty-components disj %)) 

(def el-by-id 
  #(.getElementById js/document (name %)))

(defn run-render-cycle
  []
  (do (.requestAnimationFrame js/window run-render-cycle)
      (doseq [[el component] @dirty-components] 
        (->> (component)
             (hipo/reconciliate! el)
             (log-time :render))
        (off-dirty [el component]))))

(defn mount
  [el-id component state]
  (let [el (.appendChild (el-by-id el-id) (hipo/create [:div]))]
    (hipo/set-hiccup! el [:div])
    (add-watch state :render #(mark-dirty [el component]))
    (mark-dirty [el component])))
