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
  {:tiles (->> tiles
               (map ->tile)
               shuffle)
   :sand (repeat 30 :remain)})
