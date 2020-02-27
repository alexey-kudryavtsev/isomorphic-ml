(ns emo-reply-client.core
  (:require [isomorphic-ml.python :as py]
            [isomorphic-ml.keras :as keras]
            [clojure.core.async :as async :include-macros true]
            [oops.core :refer [ocall oset!]]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(def state-atom (atom {:emoji 0}))

(def pretrained-model-uri "http://localhost:3000/load-model/model.json")

(def face-mapping {0 "angry.png"
                   1 "disgust.png"
                   2 "fear.png"
                   3 "happy.png"
                   4 "sad.png"
                   5 "surprised.png"
                   6 "neutral.png"})

(defui Video
 Object
 (componentDidUpdate [this _ _]
                     (prn "update")
                     (oset! (dom/node this)
                            "srcObject"
                            (:video-src (om/props this))))
 (componentDidMount [this]
                    (prn "mount")
                    (oset! (dom/node this)
                           "srcObject"
                           (:video-src (om/props this))))
 (render [this]
         (dom/video #js{:id       "player"
                        :width "640"
                        :height "480"
                        :autoPlay true})))

(defui Emoji
  Object
  (render [this]
          (let [emoji (face-mapping 0)]
            (dom/img #js{:src (str "emoji/" emoji)}))))

(defui Canvas
  Object
  (render [this]
          (dom/canvas nil nil)))

(defui Root
  Object
  (render [this]
          (let [{:keys [video-src]} (om/props this)]
            (dom/div 
             #js{:style #js{:display "flex" :flex-direction "row"}}
             [(dom/div #js{:style #js{:position "relative"}} 
                       [(dom/div #js{:style
                                     #js{:position "absolute"
                                         :width "240px"
                                         :height "240px"
                                         :top "130px"
                                         :left "175px"
                                         :border "2px solid white"}})
                        ((om/factory Video) {:video-src video-src})])
              (dom/div  
               #js{:style #js{:display "flex" 
                              :flex-direction "column"
                              :margin-left "16px"}}
               [(dom/button nil "Capture")
                ((om/factory Canvas))
                (dom/button nil "Guess expression")
                (dom/div 
                 #js{:style #js{:margin "30px" 
                                :width "50px"
                                :height "50px"}}
                 ((om/factory Emoji)))])]))))

(defn add-video-stream [stream]
  (prn "swapping video")
  (swap! state-atom assoc :video-src stream))

(def reconciler
  (om/reconciler {:state state-atom}))


(defn main [] 
  (let [pretrained-model (keras/load-model pretrained-model-uri)]
    (async/go 
      (async/<! (py/init))
      (let [model (async/<! pretrained-model)]
        (swap! state-atom assoc :tf-model model)))
    (ocall (ocall js/navigator "mediaDevices.getUserMedia" #js{:video true})
           "then"
           add-video-stream)
    (let [app (ocall js/document "getElementById" "app")]
      (prn "app " app)
      (om/add-root! reconciler
                    Root app))))

(main)