(ns zombieclj.game)

(def tiles
  [:h1 :h1
   :h2 :h2
   :h3 :h3
   :h4 :h4
   :h5 :h5
   :zo :zo :zo
   :fg :fg
   :gy])

(defn- ->tile [face]
  {:face face})

(defn create-game []
  {:tiles (->> tiles (mapv ->tile) shuffle)
   :sand (repeat 30 :remain)})

(defn- can-reveal? [game]
  (< (count (filter :revealed? (:tiles game))) 2))

(defn- pairs [[face count]]
  (when (= count 2) face))

(defn- find-matched-face [tiles]
  (->> tiles
       (filter :revealed?)
       (map :face)
       frequencies
       (keep pairs)
       first))

(defn- match-tile [face tile]
  (if (= face (:face tile))
    (assoc tile :matched? true)
    tile))

(defn- match-tiles [game]
  (->> game
       :tiles
       (mapv (partial match-tile (find-matched-face (:tiles game))))
       (assoc game :tiles)))

(defn reveal-tile [idx game]
  (if (can-reveal? game)
    (-> (assoc-in game [:tiles idx :revealed?] true)
        match-tiles)
    game))
