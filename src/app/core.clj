(ns app.core)

(def rover-map (atom [[0 0 0]
                      [0 0 0]
                      [0 0 0]]))
(defn define-map! [new-map]
  (reset! rover-map new-map))
(defn map-height [m]
  (count m))
(defn map-width [m]
  (count (first m)))
(defn get-map-value [m x y]
  "returns a map value from x and y"
  (nth (nth m (- (count m) y 1)) x))
(defn get-rover-map-value [rover]
  "returns the map value for the given rover position"
  (get-map-value @rover-map (:x rover) (:y rover)))

(def turns
  [:N :E :S :W])
(defn- turn [direction heading]
  "calculates the heading after turning :left or :right "
  (nth turns (mod (+ (.indexOf turns heading) (get {:right 1 :left -1} direction)) (count turns))))
(defn turn-left [rover]
  (assoc-in rover [:heading] (turn :left (:heading rover))))
(defn turn-right [rover]
  (assoc-in rover [:heading] (turn :right (:heading rover))))

(def transforms
  "map of vectors to be applied to x and y to move forward, based on the heading it can be multiplied by -1 when moving backward"
  {:N [0 1]
   :E [1 0]
   :S [0 -1]
   :W [-1 0]})
(defn- next-position-without-obstacles [multiplier rover]
  "returns the new position, without taking obstacles into account
  multiplier should be 1 when moving forward, -1 when moving backward"
    (-> rover
      (assoc-in [:x] (mod (+ (:x rover) (* (first (transforms (:heading rover))) multiplier)) (map-width @rover-map)))
      (assoc-in [:y] (mod (+ (:y rover) (* (last (transforms (:heading rover))) multiplier)) (map-height @rover-map)))))
(defn get-move-cancelling-multiplier [v]
  "returns 0 if v = 1, returns 1 if v = 0"
  (Math/abs (- 1 v)))
(defn- get-obstacle-multiplier [rover]
  "returns 0 if the rover position matches one of an obstacle, 1 otherwise"
  (Math/abs (- 1 (get-rover-map-value rover))))
(defn- move-with-obstacles [multiplier rover]
  "returns the new position, preventing the move if there's an obstacle ahead"
  (let [next-position-without-obstacles (next-position-without-obstacles multiplier rover)]
    (-> rover
        (assoc-in [:x] (+
                         (* (:x next-position-without-obstacles) (get-obstacle-multiplier next-position-without-obstacles))
                         (* (:x rover) (get-move-cancelling-multiplier (get-obstacle-multiplier next-position-without-obstacles)))))
        (assoc-in [:y] (+
                         (* (:y next-position-without-obstacles) (get-obstacle-multiplier next-position-without-obstacles))
                         (* (:y rover) (get-move-cancelling-multiplier (get-obstacle-multiplier next-position-without-obstacles))))))))
(defn move-forward [rover]
  "moves forward"
  (move-with-obstacles 1 rover))
(defn move-backward [rover]
  "moves forward"
  (move-with-obstacles -1 rover))
