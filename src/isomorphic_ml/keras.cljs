(ns isomorphic-ml.keras
  (:require
   [clojure.core.async :as async :include-macros true]
   [tfjs]
   [clojure.core.async :as async :include-macros true]
   [oops.core :refer [oget oset! ocall]]))

(defn load-model [url]
  (let [c (async/chan)]
    (ocall ((oget js/window ".tf.loadLayersModel") url) "then"
           (fn [result]
             (async/go (async/>! c result))))
    c))


(defn sequential-model []
  (ocall js/window ".tf.sequential"))

(defn dense-layer [size]
  (ocall js/window ".tf.layers.dense" #js{:units size}))

(defn conv-2d-layer [{:keys [filters kernel-size padding]
                      :or {padding "valid"}}]
  (ocall js/window ".tf.layers.conv2d" #js{:kernelSize kernel-size
                                           :filters filters
                                           :padding padding}))

(defn batch-normalization-layer []
  (ocall js/window ".tf.layers.batchNormalization"))

(defn activation-layer [activation-type]
  (ocall js/window ".tf.layers.activation" #js{:activation activation-type}))

(defn avg-pooling-2d-layer [{:keys [pool-size padding]}]
  (ocall js/window ".tf.layers.averagePooling2d" 
         #js{:poolSize pool-size
             :padding padding}))

(defn flatten-layer []
  (ocall js/window ".tf.layers.flatten"))

(defn dropout-layer [rate]
  (ocall js/window ".tf.layers.flatten" #js{:rate rate}))

(defn add-layer! [model layer]
  (ocall model "add" layer))

(defn adam-optimizer []
  (ocall js/window ".tf.train.adam"))

(defn compile-model! [model {:keys [optimizer loss metrics]}]
  (ocall model
         "compile"
         #js{:optimizer optimizer
             :loss loss
             :metrics metrics}))

(defn fit-model! [model data labels {:keys [epochs batch-size]}]
  (let [ready-chan (async/chan)] 
    (ocall 
     (ocall model
            "fit"
            data
            labels
            #js{:batch-size batch-size
                :epochs epochs})
     "then"
     (fn [result] (async/go (async/>! ready-chan result)))
     ready-chan)))

(defn predict-with-model [model input]
  (ocall model "predict" input))

(defn save-model! [model path]
  (let [ready-chan (async/chan)]
    (ocall (ocall model "save" path)
           "then"
           (fn [_]
             (async/go (async/<! ready-chan true))))
    ready-chan))