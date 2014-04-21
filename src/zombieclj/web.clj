(ns zombieclj.web
  (:require [chord.http-kit :refer [wrap-websocket-handler]]
            [clojure.core.async :refer [<! >! put! close! go go-loop timeout]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [org.httpkit.server :refer [run-server]]
            [zombieclj.game :refer [create-game tick reveal-tile game-over?]]
            [zombieclj.prep :refer [prep]]))

(defn index [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello ZombieCLJ via Compojure!"})

(defn ws-handler [{:keys [ws-channel] :as req}]
  (println "Opened connection from" (:remote-addr req))
  (let [game (atom (create-game))]
    (go-loop []
      (when-let [envelope (<! ws-channel)]
        (swap! game #(reveal-tile (:message envelope) %))
        (>! ws-channel (prep @game))
        (recur)))
    (go
      (>! ws-channel (prep @game))
      (loop []
        (<! (timeout 200))
        (swap! game tick)
        (>! ws-channel (prep @game))
        (when-not (game-over? @game)
          (recur))))))

(defroutes app
  (resources "/")
  (GET "/ws" [] (-> ws-handler
                    (wrap-websocket-handler {:format :edn})))
  (GET "/" [] index))

(defn -main [& args]
  (run-server app {:port 8666})
  (println "Started server on localhost:8666"))
