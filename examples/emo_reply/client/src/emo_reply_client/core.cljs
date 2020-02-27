(ns emo-reply-client.core
  (:require [isomorphic-ml.python :as py]
            [isomorphic-ml.keras :as keras]
            [clojure.core.async :as async :include-macros true]
            [oops.core :refer [ocall oset!]]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(def state-atom (atom {:emoji nil :pred-seq (flatten (repeat 30 [6 3]))}))

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
                     (oset! (dom/node this)
                            "srcObject"
                            (:video-src (om/props this))))
 (componentDidMount [this]
                    (swap! state-atom assoc :player this)
                    (oset! (dom/node this)
                           "srcObject"
                           (:video-src (om/props this))))
 (render [this]
         (dom/video #js{:id    "player"
                        :width "640"
                        :height "480"
                        :autoPlay true})))

(defui Emoji
  Object
  (render [this]
          (when-let [emoji (face-mapping (:emoji (om/props this)))]
            (dom/img #js{:src (str "emoji/" emoji)
                         :style (if (:big (om/props this))
                                  #js{:width "50px" :height "50px"}
                                  #js{:width "20px" :height "20px"})}))))

(defui Canvas
  Object
  (componentDidMount [this]
                     (swap! state-atom assoc :canvas this))
  (render [this]
          (dom/canvas #js{:width 240 :height 240} nil)))

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
                              :marginLeft "16px"}}
               [(dom/button 
                 #js{:onClick 
                     (fn [_]
                       (let [ctx (ocall (dom/node (:canvas @state-atom)) 
                                        "getContext" 
                                        "2d")]
                         (ocall ctx 
                                "drawImage" 
                                (dom/node (:player @state-atom)) 
                                175 130 240 240 0 0 240 240)))} 
                 "Capture")
                ((om/factory Canvas))
                (dom/button #js{:onClick 
                                (fn [_]
                                  (swap! state-atom 
                                         (fn [s]
                                           ;;FIXME: remove mock
                                           (merge 
                                            s
                                            {:emoji (first (:pred-seq s))
                                             :pred-seq (rest (:pred-seq s))}))))} 
                            "Guess expression")
                (dom/div 
                 #js{:style #js{:margin "30px" 
                                :width "80px"
                                :height "80px"}}
                 ((om/factory Emoji) {:emoji (:emoji @state-atom)
                                      :big true}))
                (dom/h4 #js{:style #js{:margin-bottom "0px"
                                       :margin-left "8px"}}
                        "Actual emotion")
                (dom/div 
                 #js{:style #js{:display "flex" :flex-direction "row"}}
                 (for [i (range 7)]
                   (dom/div #js{:style #js{:margin "8px"}}
                            ((om/factory Emoji) {:emoji i}))))])
              (dom/div #js{:style #js{:display "flex" 
                                      :flex-direction "column" 
                                      :margin-left "10px"
                                      :width "250px"}}
                       [(dom/h4 #js{:style #js{:margin-top "0px"
                                               :text-align "center"}} "Admin tools")
                        (dom/button nil "Retrain client")
                        (dom/button #js{:style #js{:margin-top "10px"}} "Retrain server")])]))))

(defn add-video-stream [stream]
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
      (om/add-root! reconciler
                    Root app))))

(main)