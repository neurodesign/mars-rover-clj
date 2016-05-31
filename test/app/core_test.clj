(ns app.core-test
  (:require [clojure.test :refer :all]
            [app.core :refer :all]))



(deftest test-map-dimensions
  (testing "map width"
    (let [m [[0 0 0]
             [0 0 0]
             [0 0 0]
             [0 0 0]]]
      (is (= 3 (map-width m)))
      (is (= 4 (map-height m))))))

(deftest test-rover-turns
  (testing "rover turns"
    (is (= {:x 0 :y 0 :heading :W} (turn-left {:x 0 :y 0 :heading :N})))
    (is (= {:x 0 :y 0 :heading :S} (turn-left {:x 0 :y 0 :heading :W})))
    (is (= {:x 0 :y 0 :heading :E} (turn-left {:x 0 :y 0 :heading :S})))
    (is (= {:x 0 :y 0 :heading :N} (turn-left {:x 0 :y 0 :heading :E})))
    (is (= {:x 0 :y 0 :heading :E} (turn-right {:x 0 :y 0 :heading :N})))))

(deftest test-move
  (testing "move forward"
    (is (= {:x 0 :y 1 :heading :N} (move-forward {:x 0 :y 0 :heading :N})))
    (is (= {:x 2 :y 1 :heading :E} (move-forward {:x 1 :y 1 :heading :E})))
    (is (= {:x 2 :y 1 :heading :S} (move-forward {:x 2 :y 2 :heading :S})))
    (is (= {:x 0 :y 1 :heading :W} (move-forward {:x 1 :y 1 :heading :W}))))
  (testing "move backward"
    (is (= {:x 0 :y 0 :heading :N} (move-backward {:x 0 :y 1 :heading :N}))))
  (testing "composition"
    (define-map! [[0 0 0]
                  [0 0 0]
                  [0 0 0]])
    (is (= {:x 0 :y 1 :heading :E} ((comp turn-right move-forward) {:x 0 :y 0 :heading :N})))
    (is (= {:x 0 :y 0 :heading :N} ((comp move-backward move-forward) {:x 0 :y 0 :heading :N})))
    (is (= {:x 1 :y 0 :heading :E} ((comp move-forward turn-right) {:x 0 :y 0 :heading :N})))
    (is (= {:x 1 :y 1 :heading :E} ((comp move-forward turn-right move-forward) {:x 0 :y 0 :heading :N})))
    (is (= {:x 1 :y 2 :heading :N} ((comp move-forward turn-left move-forward turn-right move-forward) {:x 0 :y 0 :heading :N})))
    (is (= {:x 1 :y 2 :heading :S} ((comp turn-left turn-left move-forward turn-left move-forward turn-right move-forward) {:x 0 :y 0 :heading :N})))))

(deftest test-map-limits-wrapping
  (testing "moving out of the map gets you on the opposite side"
    (is (= {:x 2 :y 0 :heading :N} (move-forward {:x 2 :y 2 :heading :N})))
    (is (= {:x 0 :y 2 :heading :E} (move-forward {:x 2 :y 2 :heading :E})))
    (is (= {:x 0 :y 1 :heading :N} ((comp move-forward move-forward move-forward move-forward) {:x 0 :y 0 :heading :N})))))

(deftest test-get-map-value
  (testing "get map value"
    (let [m [[0 0 1]
            [0 0 0]
            [0 1 0]]]
      (is (= 0 (get-map-value m 0 0)))
      (is (= 1 (get-map-value m 1 0)))
      (is (= 0 (get-map-value m 1 1)))
      (is (= 1 (get-map-value m 2 2))))))

(deftest test-get-multiplier-from-map-obstacle-value
  (testing "returns 0 if equal to 1, returns 1 if equal to 0"
    (is (= 1 (get-move-cancelling-multiplier 0)))
    (is (= 0 (get-move-cancelling-multiplier 1)))))

(deftest test-move-with-obstacles
  (define-map! [[0 1 0]
                [0 1 0]
                [0 0 0]])
  (testing "prevents moving if obstacle ahead"
    (is (= {:x 1 :y 0 :heading :N} (move-with-obstacles 1 {:x 1 :y 0 :heading :N})))
    (is (= {:x 0 :y 1 :heading :E} (move-with-obstacles 1 {:x 0 :y 1 :heading :E})))
  (testing "moves if no obstacle ahead")
    (is (= {:x 0 :y 1 :heading :N} (move-with-obstacles 1 {:x 0 :y 0 :heading :N}))))
  (testing "obstacles on the other side of the map"
    (is (= {:x 1 :y 0 :heading :N} (move-with-obstacles -1 {:x 1 :y 0 :heading :N})))))