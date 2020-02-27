(ns isomorphic-ml.keras
  (:require [libpython-clj.python :as py]
            [libpython-clj.require :as py-req]
            [clojure.core.async :as async]))

(py/initialize!)

(py-req/require-python '[tensorflow :as tf])
(py-req/require-python '[tensorflowjs :as tfjs])

(defn sequential-model []
  ((py/$.. tf/keras models Sequential)))

(defn dense-layer [size]
  ((py/$.. tf/keras layers Dense) size))

(defn conv-2d-layer [{:keys [filters kernel-size padding]
                      :or {padding "valid"}}]
  (py/$c (py/$.. tf/keras layers Conv2D) 
         :filters filters 
         :kernel_size kernel-size
         :padding padding))

(defn batch-normalization-layer []
  ((py/$.. tf/keras layers BatchNormalization)))

(defn activation-layer [activation-type]
  ((py/$.. tf/keras layers Activation) activation-type))

(defn max-pooling-2d-layer [{:keys [pool-size padding]}]
  (py/$c (py/$.. tf/keras layers MaxPooling2D) 
         :pool_size pool-size
         :padding padding)) 

(defn flatten-layer []
  ((py/$.. tf/keras layers Flatten)))

(defn dropout-layer [rate]
  ((py/$.. tf/keras layers Dropout) rate))

(defn add-layer! [model layer]
  (py/$a model add layer))

(defn adam-optimizer []
  ((py/$.. tf/keras optimizers Adam)))

(defn compile-model! [model {:keys [optimizer loss metrics]}]
  (py/$a model
         compile
         :optimizer optimizer
         :loss loss
         :metrics metrics))

(defn fit-model! [model data labels {:keys [epochs batch-size]}]
  (let [ready-chan (async/chan)]
    (async/>!! ready-chan (py/$a model
                                 fit
                                 :x data
                                 :y labels
                                 :batch-size batch-size
                                 :epochs epochs))
    ready-chan))

(defn predict-with-model [model input]
  (py/$a model predict input))

(defn save-model! [model path]
  (py/$a model save path)
  ((py/$.. tfjs/converters save_keras_model) model path))

(defn load-model [path]
  ((py/$.. tf/keras models load_model) path))

