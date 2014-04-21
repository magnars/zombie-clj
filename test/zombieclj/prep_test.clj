(ns zombieclj.prep-test
  (:require [zombieclj.prep :refer :all]
            [zombieclj.game :refer [create-game reveal-tile]]
            [midje.sweet :refer :all]))

(fact
 "Concealed tiles have no faces"

 (:tiles (prep (create-game))) => (repeat 16 {}))

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
