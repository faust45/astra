(ns astra.core
  (:import [goog History]
           [goog.history EventType])
  (:require 
   [astra.state :refer [state]]
   [weasel.repl :as wrepl]
   [goog.events :as events]
   [astra.render :refer [mount run-render-cycle]]
   [astra.ui.card :as card]))

;(wrepl/connect "ws://localhost:9003")

(run-render-cycle)
(mount :app #'card/form state)
