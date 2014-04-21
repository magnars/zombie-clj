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

(defn- peeking? [tile]
  (and (:revealed? tile)
       (not (:matched? tile))))

(defn- can-reveal? [game]
  (< (->> game
          :tiles
          (filter peeking?)
          count) 2))

(defn- pairs [[face count]]
  (when (= count 2) face))

(defn- find-matched-face [tiles]
  (->> tiles
       (filter peeking?)
       (map :face)
       frequencies
       (keep pairs)
       first))

(defn- match-tile [face tile]
  (if (= face (:face tile))
    (assoc tile :matched? true)
    tile))

(defn- zombify-graveyard [tiles]
  (map #(if (= :gy (:face %))
          (assoc % :face :zo)
          %) tiles))

(defn- apply-face-event [face game]
  (case face
    :fg (assoc game :foggy? true)
    :zo (update-in game [:tiles] zombify-graveyard)
    game))

(defn- match-tiles [game]
  (let [matched (find-matched-face (:tiles game))]
    (->> game
         :tiles
         (mapv (partial match-tile matched))
         (assoc game :tiles)
         (apply-face-event matched))))

(defn reveal-tile [idx game]
  (if (can-reveal? game)
    (-> (assoc-in game [:tiles idx :revealed?] true)
        match-tiles)
    game))
