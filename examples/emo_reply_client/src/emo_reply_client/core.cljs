(ns emo-reply-client.core
  (:require [isomorphic-ml.python :as py]
            [isomorphic-ml.keras :as keras]
            [clojure.core.async :as async :include-macros true]))

(def pretrained-model-uri "http://localhost:3000/load-model/model.json")

(defn main [] 
  (let [pretrained-model (keras/load-model pretrained-model-uri)]
    (async/go 
      (let [model (async/<! pretrained-model)]
        (prn "Model loaded")
        (set! js/window.tfmodel model)))))

(async/go
  (async/<! (py/init))
  (main))