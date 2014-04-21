(ns zombieclj.web
  (:require [chord.http-kit :refer [wrap-websocket-handler]]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [org.httpkit.server :refer [run-server]]
            [zombieclj.game :refer [create-game]]
            [zombieclj.prep :refer [prep]]))

(defn index [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello ZombieCLJ via Compojure!"})

(defn ws-handler [{:keys [ws-channel] :as req}]
  (println "Opened connection from" (:remote-addr req))
  (let [game (create-game)]
    (go
      (>! ws-channel (prep game)))))

(defroutes app
  (resources "/")
  (GET "/ws" [] (-> ws-handler
                    (wrap-websocket-handler {:format :edn})))
  (GET "/" [] index))

(defn -main [& args]
  (run-server app {:port 8666})
  (println "Started server on localhost:8666"))
