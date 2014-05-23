(ns zombieclj.game-loop
  (:require [clojure.core.async :refer [<! >! go go-loop timeout]]
            [zombieclj.game :refer [create-game tick reveal-tile game-over?]]
            [zombieclj.prep :refer [prep]]))

(defn start-game-loop [game from-player to-player]
  (let [game (atom game)]
    (go-loop []
      (when-let [tile (<! from-player)]
        (swap! game #(reveal-tile tile %))
        (>! to-player (prep @game))
        (recur)))
    (go
      (>! to-player (prep @game))
      (loop []
        (<! (timeout 200))
        (swap! game tick)
        (>! to-player (prep @game))
        (when-not (game-over? @game)
          (recur))))))
