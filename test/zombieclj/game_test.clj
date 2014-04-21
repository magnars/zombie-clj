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

(def sample-game
  {:tiles [{:face :gy} {:face :h1} {:face :h2} {:face :h4}
           {:face :h3} {:face :fg} {:face :h5} {:face :zo}
           {:face :h5} {:face :h2} {:face :h4} {:face :zo}
           {:face :h1} {:face :fg} {:face :h3} {:face :zo}]
   :sand (repeat 30 :remain)})

(fact "You can reveal a tile"
      (->> sample-game
           (reveal-tile 1)
           :tiles
           (filter :revealed?))
      => [{:face :h1, :revealed? true}])

(fact "You can't reveal the third tile"
      (->> sample-game
           (reveal-tile 1)
           (reveal-tile 2)
           (reveal-tile 3)
           :tiles
           (filter :revealed?))
      => [{:face :h1, :revealed? true}
          {:face :h2, :revealed? true}])

(fact "You can reveal two tiles"
      (->> sample-game
           (reveal-tile 1)
           (reveal-tile 12)
           :tiles
           (filter :matched?))
      => [{:face :h1, :revealed? true, :matched? true}
          {:face :h1, :revealed? true, :matched? true}])


