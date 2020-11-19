(ns astra.ui.ui-test
  (:require
   [hiccup-find.core :as hf]
   [astra.ui.card :as card]
   [astra.state :as state :refer [emit state]]
   [clojure.test :refer [deftest is]]))

(deftest test-validation
  (is (= [:card/number :card/format-num-or-space]
         (state/validate-attr :card/number "eee"))))

(deftest test-input-validation
  (reset! state {})

  (emit :card :input {:attr "number" :value ""})
  (is (= (:errors @state) {:card {:number :v/not-empty}}))

  (emit :card :input {:attr "number" :value "eee"})
  (is (= (:errors @state) {:card {:number :card/format-num-or-space}}))

  (emit :card :input {:attr "number" :value "12234"})
  (is (= (:errors @state) {:card {:number :card/requires-16-numbers}}))

  (emit :card :input {:attr "number" :value "1234 1234 1234 1234"})
  (is (= (:errors @state) {:card {}})))

(deftest test-input
  (reset! state {})

  (let [card-number "1234 1234 1234 1234"]
    (emit :card :input {:attr "number" :value card-number})
    (is (= (:card @state) {:number card-number}))))


(deftest test-form
  (reset! state {})
  (emit :card :input {:attr "number" :value "eee"})

  (let [form (card/form)]
    (is (not (empty? (hf/hiccup-find [:div.invalid.form-row] form))))))
