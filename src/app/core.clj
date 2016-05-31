(ns app.core)

(def map-size (atom {:width 20 :height 20}))

(defn change-map-size [new-size]
  (reset! map-size new-size))

(def turns
  [:N :E :S :W])

(defn- turn [direction heading]
  "turn :left or :right"
  (nth turns (mod (+ (.indexOf turns heading) (get {:right 1 :left -1} direction)) (count turns))))

(defn turn-left [rover]
  (assoc-in rover [:heading] (turn :left (:heading rover))))
(defn turn-right [rover]
  (assoc-in rover [:heading] (turn :right (:heading rover))))

(def transforms
  {:N [0 1]
   :E [1 0]
   :S [0 -1]
   :W [-1 0]})

(defn- move-forward-n [n rover]
  (-> rover
    (assoc-in [:x] (mod (+ (:x rover) (* (first (transforms (:heading rover))) n)) (:width @map-size)))
    (assoc-in [:y] (mod (+ (:y rover) (* (last (transforms (:heading rover))) n)) (:height @map-size)))))

(defn move-forward-once [rover]
  (move-forward-n 1 rover))

(defn move-forward-many [n]
  (fn [rover] (move-forward-n n rover)))