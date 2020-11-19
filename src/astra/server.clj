(ns astra.server
  (:use ring.adapter.jetty)
  (:require
   [clojure.string :as s]
   [reitit.ring :as r]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(defn response
  [data]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body data})

(def clean-spaces #(s/replace % " " ""))
(def is-valid-format? #(->> % (re-matches #"\d{16}") empty? not))
(defn str-to-arr-num
  [s]
  (map #(Character/getNumericValue %) s))

(defn x2
  [num]
  (let [d (* 2 num)]
    (if (< d 9)
      d
      (- d 9))))

(defn x2-every-second
  [idx num]
  (if (odd? idx)
    (x2 num)
    num))

(def sum #(reduce + 0 %))

(defn calc-luhn-sum
  [arr-num]
  (->> arr-num reverse (map-indexed x2-every-second) sum))

(defn is-valid-card?
  [card-number]
  (let [s (-> card-number str clean-spaces)]
    (if (is-valid-format? s)
      (-> s str-to-arr-num calc-luhn-sum (mod 10) zero?)
      false)))

(defn validate-card
  [{:keys [body] :as req}]
  (let [card-number (get body "card-number")]
    (response {:is-valid (is-valid-card? card-number)})))

(def routes
  [["/validate-card" {:post validate-card}]])

(def app
  (let [opts {:data
              {:middleware
               [wrap-json-body wrap-json-response wrap-params]}}
        assets (r/routes
                (r/create-file-handler {:path "/" :root "resources/public/"})
                (r/create-default-handler))]
    (-> routes
        (r/router opts)
        (r/ring-handler assets))))

(defn -main
  []
  (run-jetty app {:port 3000}))
