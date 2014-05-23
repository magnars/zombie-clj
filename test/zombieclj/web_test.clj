(ns zombieclj.web-test
  (:require [clojure.core.async :refer [chan go >! <!!]]
            [midje.sweet :refer :all]
            [zombieclj.web :refer :all]))

(fact "Unwraps envelope"
      (let [ws-chan (chan)
            proxy (envelope-unwrapper ws-chan)]
        (go
          (>! ws-chan {:message "lol"})
          (>! ws-chan {:message "lol!!"}))
        (<!! proxy) => "lol"
        (<!! proxy) => "lol!!"))
