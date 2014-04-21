(ns zombieclj.game-test
  (:require [zombieclj.game :refer :all]
            [midje.sweet :refer :all]))

(fact "Creates a game board"
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
  {:ticks 0
   :tiles [{:face :gy} {:face :h1} {:face :h2} {:face :h4}
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
      => [{:face :h1, :revealed? true, :remaining-ticks 5}
          {:face :h2, :revealed? true, :remaining-ticks 5}])

(fact "You can reveal two tiles"
      (->> sample-game
           (reveal-tile 1)
           (reveal-tile 12)
           :tiles
           (filter :matched?))
      => [{:face :h1, :revealed? true, :matched? true}
          {:face :h1, :revealed? true, :matched? true}])

(def sample-game
  {:ticks 0
   :tiles [{:face :gy} {:face :h1} {:face :h2} {:face :h4}
           {:face :h3} {:face :fg} {:face :h5} {:face :zo}
           {:face :h5} {:face :h2} {:face :h4} {:face :zo}
           {:face :h1} {:face :fg} {:face :h3} {:face :zo}]
   :sand (repeat 30 :remain)})

(fact "You can survive the game"
      (->> sample-game
           (reveal-tile 1)
           (reveal-tile 12)
           (reveal-tile 2)
           (reveal-tile 9)
           (reveal-tile 3)
           (reveal-tile 10)
           (reveal-tile 4)
           (reveal-tile 14)
           (reveal-tile 6)
           (reveal-tile 8)
           :safe?)
      => true)

(fact "You can match multiple pairs"
      (->> sample-game
           (reveal-tile 1)
           (reveal-tile 12)
           (reveal-tile 2)
           (reveal-tile 9)
           :tiles
           (filter :matched?))
      => [{:face :h1, :revealed? true, :matched? true}
          {:face :h2, :revealed? true, :matched? true}
          {:face :h2, :revealed? true, :matched? true}
          {:face :h1, :revealed? true, :matched? true}])

(fact "When matching fog, the game board becomes foggy"
      (->> sample-game
           (reveal-tile 5)
           (reveal-tile 13)
           :foggy?) => true)

(fact "When matching zombies, graveyard becomes zombie"
      (->> sample-game
           (reveal-tile 7)
           (reveal-tile 11)
           :tiles
           (map :face)
           frequencies) => {:h1 2
                            :h2 2
                            :h3 2
                            :h4 2
                            :h5 2
                            :fg 2
                            :zo 4})

(fact "When matching zombies, only revealed zombies become matched"
      (->> sample-game
           (reveal-tile 7)
           (reveal-tile 11)
           :tiles
           (filter :matched?)
           count) => 2)

(fact "Revealing a pair of flesh gobbling zombies eats time"
      (->> sample-game
           (reveal-tile 7)
           (reveal-tile 11)
           :sand
           (take 4)) => [:zombie :zombie :zombie :remain])

(defn- tick-x-times [ticks game]
  (->> game (iterate tick) (drop ticks) first))

(fact "Revealing zombies at the end of the game does not add sand"
      (->> sample-game
           (tick-x-times 145)
           (reveal-tile 7)
           (reveal-tile 11)
           :sand
           count) => 30)

(fact "Revealing zombies hastens the end of the world"
      (->> sample-game
           (reveal-tile 7)
           (reveal-tile 11)
           (tick-x-times 136)
           :dead?) => true)

(fact
 "Tick decrements remaining ticks on peeking tiles"

 (->> sample-game
      (reveal-tile 1)
      (reveal-tile 2)
      (tick)
      :tiles
      (filter :revealed?))
 => [{:face :h1, :revealed? true, :remaining-ticks 4}
     {:face :h2, :revealed? true, :remaining-ticks 4}])

(fact
 "Different revealed tiles are turned back after two ticks"

 (->> sample-game
      (reveal-tile 1)
      (reveal-tile 2)
      (tick)
      (tick)
      :tiles
      (filter :revealed?))
 => [])

(fact
 "One sand is removed for every 5 ticks"
 (->> sample-game
      (tick-x-times 5)
      :sand
      (take 2))
 => [:gone :remain])

(fact
 "All sand is removed after 150 ticks"
 (->> sample-game
      (tick-x-times 150)
      :sand
      frequencies)
 => {:gone 30})

(fact
 "The game is over after 150 ticks, and the player is fucking dead."
 (->> sample-game
      (tick-x-times 151)
      :dead?)
 => true)

(fact
 "game over when dead"
 (->> sample-game
      (tick-x-times 151)
      game-over?)
 => true)

(fact "Game over when safe"
      (->> sample-game
           (reveal-tile 1)
           (reveal-tile 12)
           (reveal-tile 2)
           (reveal-tile 9)
           (reveal-tile 3)
           (reveal-tile 10)
           (reveal-tile 4)
           (reveal-tile 14)
           (reveal-tile 6)
           (reveal-tile 8)
           game-over?)
      => true)

(fact "Not game over"
      (->> sample-game
           game-over?)
      => nil)
