(ns astra.net
  (:require 
   [clojure.browser.event :as event]
   [clojure.browser.net :as net]
   [cljs.core.async :refer [chan put! >! <! timeout close!]]
   [cljs.core.async :refer-macros [go go-loop]]
   [astra.utils :refer [log log-pr p]]))

(defn clj->json
  [ds]
  (->> ds
       clj->js
       (.stringify js/JSON)))

(defn resp-handler
  [ch ev]
  (let [target (.-target ev)]
    (if (.isSuccess target)
      (put! ch (js->clj (.getResponseJson target)))
      (close! ch))))

(defn send-req
  [uri data]
  (let [req (net/xhr-connection)
        ch (chan)]
    (event/listen req (:complete net/event-types) #(resp-handler ch %))
    (net/transmit req uri "POST"
                  (clj->json data) {"Content-Type" "application/json"})
    ch))
