(ns zombieclj.game
  (:require [clojure.set :as set]))

(def game-length 30)
(def ticks-per-sand 5)
(def peeking-ticks 2)

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
   :sand (repeat game-length :remain)})

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

(defn- convert-sand [sand convert-to]
  (take 30 (concat (take-while #(not= % :remain) sand)
                   convert-to
                   (drop (count convert-to) (drop-while #(not= % :remain) sand)))))

(defn- apply-face-event [face game]
  (case face
    :fg (assoc game :foggy? true)
    :zo (-> game
            (update-in [:tiles] zombify-graveyard)
            (update-in [:sand] #(convert-sand % (repeat 3 :zombie))))
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
    (assoc tile :remaining-ticks peeking-ticks)
    tile))

(defn- init-expiry [game]
  (if (= 2 (num-peeking game))
    (update-in game [:tiles] #(map init-ticks %))
    game))

(defn- all-houses-matched? [tiles]
  (->> tiles
       (filter :matched?)
       (map :face)
       set
       (set/subset? #{:h1 :h2 :h3 :h4 :h5})))

(defn- update-survival-status [game]
  (if (all-houses-matched? (:tiles game))
    (assoc game :safe? true)
    game))

(defn reveal-tile [idx game]
  (if (can-reveal? game)
    (-> game
        (assoc-in [:tiles idx :revealed?] true)
        match-tiles
        init-expiry
        update-survival-status)
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

(defn- count-down [game]
  (if (= 0 (mod (:ticks game) ticks-per-sand))
    (update-in game [:sand] #(convert-sand % [:gone]))
    game))

(defn tick [game]
  (if (nil? (:remain (frequencies (:sand game))))
    (assoc game :dead? true)
    (-> game
        (update-in [:ticks] inc)
        count-down
        (update-in [:tiles] dec-remaining-ticks)
        (update-in [:tiles] conceal-expired-tiles))))

(defn game-over? [game]
  (or (:dead? game)
      (:safe? game)))
