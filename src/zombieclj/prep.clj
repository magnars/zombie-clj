(ns zombieclj.prep)

(defn- conceal-face [tile]
  (if (:revealed? tile)
    tile
    (dissoc tile :face)))

(defn- prep-tiles [tiles]
  (->> tiles
       (map conceal-face)
       (map #(dissoc % :remaining-ticks))))

(defn prep [game]
  (-> game
      (update-in [:tiles] prep-tiles)
      (dissoc :ticks)))
