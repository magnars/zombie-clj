(ns zombieclj.game-test
  (:require [zombieclj.game :refer :all]
            [midje.sweet :refer :all]))

(fact "Creates a game"
      (->> (create-game)
           :tiles
           (map :face)
           frequencies) => {:h1 2
                            :h2 2
                            :h3 2
                            :h4 2
                            :h5 2
                            :fg 2
                            :gy 1
                            :zo 3})

(fact "Creates random games"
      (count (set (repeatedly 100 create-game))) => #(> % 1))

(fact "Creates game with sand"
      (-> (create-game) :sand frequencies) => {:remain 30})
