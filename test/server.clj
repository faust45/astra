(ns astra.server.test
  (:require
   [clojure.test :refer :all]
   [astra.server :as serv]))


(deftest test-calc-luhn
  (testing "valid case"
    (is (= false (serv/calc-luhn "12345678")))))

(run-tests)
