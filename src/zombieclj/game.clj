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
  {:ticks 0
   :tiles (->> tiles (mapv ->tile) shuffle)
   :sand (repeat 30 :remain)})

(defn- peeking? [tile]
  (and (:revealed? tile)
       (not (:matched? tile))))

(defn- num-peeking [game]
  (->> game :tiles (filter peeking?) count))

(defn- can-reveal? [game]
  (< (num-peeking game) 2))

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

(defn- init-ticks [tile]
  (if (peeking? tile)
    (assoc tile :remaining-ticks 2)
    tile))

(defn- init-expiry [game]
  (if (= 2 (num-peeking game))
    (update-in game [:tiles] #(map init-ticks %))
    game))

(defn reveal-tile [idx game]
  (if (can-reveal? game)
    (-> game
        (assoc-in [:tiles idx :revealed?] true)
        match-tiles
        init-expiry)
    game))

(defn- nil-safe-dec [num]
  (and num (dec num)))

(defn- dec-remaining-ticks [tiles]
  (mapv #(update-in % [:remaining-ticks] nil-safe-dec) tiles))

(defn- conceal-expired-tile [tile]
  (if (= 0 (:remaining-ticks tile))
    (dissoc tile :remaining-ticks :revealed?)
    tile))

(defn- conceal-expired-tiles [tiles]
  (mapv conceal-expired-tile tiles))

(defn- goneify-remain [sand]
  (concat (take-while #(not= % :remain) sand)
          [:gone]
          (drop 1 (drop-while #(not= % :remain) sand))))

(defn- count-down [game]
  (if (= 0 (mod (:ticks game) 5))
    (update-in game [:sand] goneify-remain)
    game))

(defn tick [game]
  (if (= (:ticks game) 150)
    (assoc game :dead? true)
    (-> game
        (update-in [:ticks] inc)
        count-down
        (update-in [:tiles] dec-remaining-ticks)
        (update-in [:tiles] conceal-expired-tiles))))
