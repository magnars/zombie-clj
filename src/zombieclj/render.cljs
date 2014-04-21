(ns zombieclj.render
  (:require [quiescent :as q :include-macros true]
            [quiescent.dom :as d]))

(def game {:foggy? true
            :tiles [{} {} {}
                    {:face :h1 :revealed? true}
                    {} {}
                    {:face :fg :revealed? true :matched? true}
                    {:face :fg :revealed? true :matched? true}
                    {} {} {} {} {} {} {} {}]
           :sand (concat
                  (repeat 5 :gone)
                  (repeat 25 :remain))})

(q/defcomponent Cell [tile]
  (d/div {:className "cell"}
         (d/div {:className (str "tile"
                                 (when (:revealed? tile) " revealed")
                                 (when (:matched? tile) " match"))}
                (d/div {:className "front"})
                (d/div {:className (str "back "
                                        (when-let [face (:face tile)]
                                          (name face)))}))))

(q/defcomponent Line [tiles]
  (apply d/div {:className "line"}
         (map Cell tiles)))

(q/defcomponent Board [game]
  (apply d/div {:className (str "board clearfix"
                                (when (:foggy? game)
                                  " foggy"))}
         (map Line (partition 4 (:tiles game)))))

(q/defcomponent Sand [sand]
  (d/div {:className (str "sand " (name sand))}))

(q/defcomponent Hourglass [sand]
  (apply d/div {:className "timer"}
         (map Sand sand)))

(q/defcomponent Game [game]
  (d/div {}
   (Board game)
   (Hourglass (:sand game))))

(q/render (Game game)
          (.getElementById js/document "main"))
