(ns zombieclj.web
  (:require [chord.http-kit :refer [wrap-websocket-handler]]
            [clojure.core.async :refer [<! >! go-loop chan]]
            [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [org.httpkit.server :refer [run-server]]
            [zombieclj.game-loop :refer [start-game-loop]]
            [zombieclj.game :refer [create-game]]))

(defn envelope-unwrapper [ws-chan]
  (let [proxy (chan)]
    (go-loop []
      (when-let [envelope (<! ws-chan)]
        (>! proxy (:message envelope))
        (recur)))
    proxy))

(defn ws-handler [{:keys [ws-channel] :as req}]
  (println "Opened connection from" (:remote-addr req))
  (start-game-loop (create-game) (envelope-unwrapper ws-channel) ws-channel))

(defroutes app
  (resources "/")
  (GET "/ws" [] (-> ws-handler
                    (wrap-websocket-handler {:format :edn})))
  (GET "/" [] (fn [_] (slurp (io/resource "public/index.html")))))

(defn -main [& args]
  (run-server app {:port 8666})
  (println "Started server on localhost:8666"))
