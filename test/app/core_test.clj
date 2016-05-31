(ns app.core-test
  (:require [clojure.test :refer :all]
            [app.core :refer :all]))

(deftest test-rover-turns
  (testing "rover turns"
    (is (= {:x 0 :y 0 :heading :W} (turn-left {:x 0 :y 0 :heading :N})))
    (is (= {:x 0 :y 0 :heading :S} (turn-left {:x 0 :y 0 :heading :W})))
    (is (= {:x 0 :y 0 :heading :E} (turn-left {:x 0 :y 0 :heading :S})))
    (is (= {:x 0 :y 0 :heading :N} (turn-left {:x 0 :y 0 :heading :E})))
    (is (= {:x 0 :y 0 :heading :E} (turn-right {:x 0 :y 0 :heading :N})))))

(deftest test-move-forward
  (testing "move forward"
    (is (= {:x 0 :y 1 :heading :N} (move-forward-once {:x 0 :y 0 :heading :N})))
    (is (= {:x 2 :y 1 :heading :E} (move-forward-once {:x 1 :y 1 :heading :E})))
    (is (= {:x 2 :y 1 :heading :S} (move-forward-once {:x 2 :y 2 :heading :S})))
    (is (= {:x 2 :y 3 :heading :W} (move-forward-once {:x 3 :y 3 :heading :W}))))
  (testing "move forward many"
    (is (= {:x 0 :y 12 :heading :N} ((move-forward-many 12) {:x 0 :y 0 :heading :N}))))
  (testing "composition"
    (is (= {:x 0 :y 1 :heading :E} ((comp turn-right move-forward-once) {:x 0 :y 0 :heading :N})))
    (is (= {:x 1 :y 2 :heading :N} ((comp move-forward-once turn-left move-forward-once turn-right move-forward-once) {:x 0 :y 0 :heading :N}))))
    (is (= {:x 1 :y 2 :heading :S} ((comp turn-left turn-left move-forward-once turn-left move-forward-once turn-right move-forward-once) {:x 0 :y 0 :heading :N}))))

(deftest test-map-limits-cycles
  (testing "moving out of the map gets you on the opposite side")
  (is (= {:x 19 :y 0 :heading :N} (move-forward-once {:x 19 :y 19 :heading :N})))
  (is (= {:x 0 :y 19 :heading :E} (move-forward-once {:x 19 :y 19 :heading :E})))
  (change-map-size {:width 100 :height 100})
  (is (= {:x 99 :y 0 :heading :N} (move-forward-once {:x 99 :y 99 :heading :N})))
  (is (= {:x 99 :y 9 :heading :N} ((move-forward-many 10) {:x 99 :y 99 :heading :N}))))