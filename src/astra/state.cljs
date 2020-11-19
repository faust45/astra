(ns astra.state
  (:require
   [astra.utils :refer [log]]
   [cljs.spec.alpha :as sp]))
   

(def state (atom {}))

(sp/def :v/not-empty not-empty)
(sp/def :v/int #(re-matches #"\d+" %)) 

(defn is-valid?
  [comp]
  (-> @state :errors comp empty?))

(defonce human-errors
  {:v/not-empty "Не может быть пустым"
   :v/int "Только числа"
   :card/format-num-or-space "Только цифры и пробелы"
   :card/requires-16-numbers "Поле должно содержать ровно 16 знаков"})

(defn errors-for
  [path]
  [(->> path (get-in (:errors @state)) human-errors)])

(def valid? #(-> % :errors not))

(defn validate-attr
  [vform value]
  (->> value 
       (sp/explain-data vform)
       :cljs.spec.alpha/problems
       first :via))

(defn validate
  [{:keys [comp attr value] :as ev}]
  (let [p-attr (keyword comp attr)
        errors (validate-attr p-attr value)]
    (merge {:errors errors} ev)))

(defn update-errors
  [{:keys [comp attr value errors]}]
  (if (empty? errors)
    (swap! state update-in [:errors comp] dissoc (keyword attr))
    (->> errors
         last
         (swap! state assoc-in [:errors comp (keyword attr)]))))

(defn update-state
  [{:keys [attr value comp] :as ev}]
  (swap! state assoc-in [comp (keyword attr)] value))

(defn get-dispatch-fn
  [ns]
  (.. (->> ns name (str "astra.ui.") symbol find-ns) -obj -action))

(defmulti process-ev :type)  
(defmethod process-ev :action
  [{:keys [comp data] :as ev}]
  (if-let [dispatch-fn (get-dispatch-fn comp)]
    (dispatch-fn data ev)))

(defmethod process-ev :state
  [{:keys [comp data] :as ev}]
  (swap! state update-in [comp] merge data))

(defmethod process-ev :input
  [{:keys [data] :as ev}]
  (let [e (-> ev
              (merge data)
              (dissoc :data)
              validate)]
    (update-errors e)
    (when (valid? e)
      (update-state e))))

(defn target-value
  [e]
  (let [target (.. e -target)
        attr (keyword (.. target -name))
        value (.. target -value)]
    {:attr (keyword attr) :value value}))

(defn emit
  [component event data]
  (process-ev {:type event :comp component :data data}))

(defn force-re-render
  []
  (->
   #(swap! state update-in [:hot-reload] inc)
   (js/setTimeout 200)))
