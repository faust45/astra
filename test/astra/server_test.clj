(ns astra.server-test
  (:require
   [clojure.test :refer :all]
   [astra.server :as serv]))

(def valid-card-arr [4 5 3 9 1 4 8 8 0 3 4 3 6 4 6 7])
(def invalid-card-arr [8 2 7 3 1 2 3 2 7 3 5 2 0 5 6 9])
(def valid-card "4539 1488 0343 6467")
(def invalid-card "8273 1232 7352 0569")

(deftest test-calc-luhn
  (is (= 4  (serv/calc-luhn-sum [1 2])))
  (is (= 14 (serv/calc-luhn-sum [1 2 3 4])))
  (is (= 21 (serv/calc-luhn-sum [1 2 3 4 5])))
  (is (= 21 (serv/calc-luhn-sum [1 2 3 4 5 6])))
  (is (= 80 (serv/calc-luhn-sum valid-card-arr)))
  (is (= 57 (serv/calc-luhn-sum invalid-card-arr))))

(deftest test-validate-format
  (is (= true (-> valid-card serv/clean-spaces serv/is-valid-format?)))
  (is (= false (-> "4539 1488 0343 646"
                   serv/clean-spaces serv/is-valid-format?)))
  (is (= false (-> "e539 1488 0343 6466"
                   serv/clean-spaces serv/is-valid-format?))))

(deftest test-is-valid-card?
  (is (= true (-> valid-card-arr serv/calc-luhn-sum (mod 10) zero?)))
  (is (= true (serv/is-valid-card? valid-card)))
  (is (= false (serv/is-valid-card? invalid-card))))

(deftest test-validate-card-action-response
  (let [valid-response
        {:status 200
         :headers {"Content-Type" "application/json"}
         :body {:is-valid true}}
        invalid-response (assoc-in valid-response [:body :is-valid] false)]
    (is (= valid-response
           (serv/validate-card {:body {"card-number" valid-card}})))
    (is (= invalid-response
           (serv/validate-card {:body {"card-number" invalid-card}})))))

(deftest test-empty-body-req 
  (let [response
        {:status 200
         :headers {"Content-Type" "application/json"}
         :body {:is-valid false}}]
    (is (= response (serv/validate-card {})))))

(deftest test-ring-req
  (let [valid-card-req {:request-method :post
                        :uri "/validate-card"
                        :headers {"Content-Type" "application/json"}
                        :body {"card-number" (str valid-card)}}
        invalid-card-req {:request-method :post
                          :uri "/validate-card"
                          :headers {"Content-Type" "application/json"}
                          :body ""}
        valid-card-resp {:status 200
                         :headers {"Content-Type" "application/json"}
                         :body "{\"is-valid\":true}"}
        invalid-card-resp {:status 200
                           :headers {"Content-Type" "application/json"}
                           :body "{\"is-valid\":false}"}]
    (is (= valid-card-resp (serv/app valid-card-req)))
    (is (= invalid-card-resp (serv/app invalid-card-req)))))
