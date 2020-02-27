(ns emo-reply-cnn.core
  (:require [isomorphic-ml.keras :as keras]))

(defn create-emo-recognition-model []
  (-> (keras/sequential-model)
      ;; 1st convolutional layer
      (keras/add-layer! (keras/conv-2d-layer {:filters 64
                                              :padding "same"
                                              :kernel  [3,3]}))
      (keras/add-layer! (keras/batch-normalization-layer))
      (keras/add-layer! (keras/activation-layer "relu"))
      (keras/add-layer! (keras/max-pooling-2d-layer {:pool-size [2,2]
                                                     :padding   "valid"}))
      (keras/add-layer! (keras/dropout-layer 0.25))
      
      ;; 2nd convolutional layer
      (keras/add-layer! (keras/conv-2d-layer {:filters 128
                                              :padding "same"
                                              :kernel  [3,3]}))
      (keras/add-layer! (keras/batch-normalization-layer))
      (keras/add-layer! (keras/activation-layer "relu"))
      (keras/add-layer! (keras/max-pooling-2d-layer {:pool-size [2,2]
                                                     :padding   "valid"}))
      (keras/add-layer! (keras/dropout-layer 0.25))
      
      ;; 3d convolutional layer
      (keras/add-layer! (keras/conv-2d-layer {:filters 256
                                              :padding "same"
                                              :kernel  [3,3]}))
      (keras/add-layer! (keras/batch-normalization-layer))
      (keras/add-layer! (keras/activation-layer "relu"))
      (keras/add-layer! (keras/max-pooling-2d-layer {:pool-size [2,2]
                                                     :padding   "valid"}))
      (keras/add-layer! (keras/dropout-layer 0.25))
      
      ;; 4th convolutional layer
      (keras/add-layer! (keras/conv-2d-layer {:filters 512
                                              :padding "same"
                                              :kernel  [3,3]}))
      (keras/add-layer! (keras/batch-normalization-layer))
      (keras/add-layer! (keras/activation-layer "relu"))
      (keras/add-layer! (keras/max-pooling-2d-layer {:pool-size [2,2]
                                                     :padding   "valid"}))
      (keras/add-layer! (keras/dropout-layer 0.25))
      
      (keras/add-layer! (keras/flatten-layer))
      
      ;; 1st dense layer
      (keras/add-layer! (keras/dense-layer 256))
      (keras/add-layer! (keras/batch-normalization-layer))
      (keras/add-layer! (keras/activation-layer "relu"))
      (keras/add-layer! (keras/dropout-layer 0.25))
      
      ;; 2nd dense layer
      (keras/add-layer! (keras/dense-layer 512))
      (keras/add-layer! (keras/batch-normalization-layer))
      (keras/add-layer! (keras/activation-layer "relu"))
      (keras/add-layer! (keras/dropout-layer 0.25))
      
      ;; Output
      (keras/add-layer! (keras/dense-layer 7))
      (keras/add-layer! (keras/activation-layer "softmax"))
      (keras/compile-model! {:optimizer (keras/adam-optimizer) 
                             :loss "categorical_crossentropy" 
                             :metrics ["accuracy"]})))


(defn train-emo-recognition-model [model data labels epochs]
  (keras/fit-model! model data labels {:epochs epochs
                                       :batch-size 50}))
