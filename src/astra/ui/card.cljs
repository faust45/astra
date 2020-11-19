(ns astra.ui.card
  (:require 
   [astra.net :as net]
   [cljs.core.async :refer [chan put! >! <! timeout close!]]
   [cljs.core.async :refer-macros [go go-loop]]
   [astra.utils :refer [log log-pr p]]
   [cljs.spec.alpha :as sp]
   [astra.state :refer [state errors-for emit target-value is-valid?]]
   [clojure.string :refer [join split replace]]))


(def clean-spaces #(replace % " " ""))

(sp/def :card/requires-16-numbers
  #(->> % clean-spaces (re-matches #"^\d{16}"))) 
(sp/def :card/format-num-or-space #(->> % clean-spaces (re-matches #"\d+"))) 
(sp/def :card/number (sp/and :v/not-empty
                             :card/format-num-or-space
                             :card/requires-16-numbers))  

(defn card
  [key]
  (-> @state :card key))

(defmulti action identity)

(defmethod action :validate-number
  []
  (go
    (let [data {:card-number (card :number)}
          ch (net/send-req "/validate-card" data)
          resp (<! ch)
          is-valid (get resp "is-valid")]
      (emit :card :state {:is-valid is-valid :show-alert true})
      (<! (timeout 2000))
      (emit :card :state {:show-alert false}))))

(defn errors
  [messages]
  (if messages
    [:div.invalid.form-row
     [:div {:class "col-sm-10"}
      [:h8 {} (first messages)]]]))

(defn form 
  []
  [:div.container
   (if (card :show-alert)
     [:div.alert.alert-primary
      (if (card :is-valid)
        "Card number is valid!"
        "Card number is invalid!")])
   [:form
    [:div {:class "form-group"}
     [:label "Card number: "]
     [:input {:class "form-control"
              :value (card :number) 
              :onchange #(->> % target-value (emit :card :input))
              :name "number"}]
     (-> [:card :number] errors-for errors)]]
   [:div {:class "form-group"}
    [:button.btn.btn-outline-primary
     {:disabled (-> :card is-valid? not) 
      :onclick #(emit :card :action :validate-number)}
     "validate card"]]])
