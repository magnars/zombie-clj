(ns zombieclj.prep-test
  (:require [zombieclj.prep :refer :all]
            [zombieclj.game :refer [create-game reveal-tile]]
            [midje.sweet :refer :all]))

(fact
 "Concealed tiles have no faces"

 (->> (create-game)
      prep
      :tiles
      (map #(dissoc % :id))) => (repeat 16 {}))

(fact
 "Revealed tiles has faces"

 (->> (create-game)
      (reveal-tile 1)
      prep
      :tiles
      (filter :face)
      count) => 1)

(fact
 "No ticks"

 (:ticks (prep (create-game))) => nil)

(fact
 "No remaining-ticks"

 (->> (create-game)
      (reveal-tile 1)
      (reveal-tile 2)
      prep
      :tiles
      (filter :remaining-ticks)) => [])

(fact "Provides id for each tile"
      (->> (create-game)
           prep
           :tiles
           (map :id)) => (range 0 16))
