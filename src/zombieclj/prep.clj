(ns zombieclj.prep)

(defn- conceal-face [tile]
  (if (or (:revealed? tile)
          (and (:remaining-ticks tile) (> (:remaining-ticks tile) 0)))
    tile
    (dissoc tile :face)))

(defn- prep-tiles [tiles]
  (->> tiles
       (map conceal-face)
       (map #(dissoc % :remaining-ticks))
       (map-indexed #(assoc %2 :id %1))))

(defn prep [game]
  (-> game
      (update-in [:tiles] prep-tiles)
      (dissoc :ticks)))
