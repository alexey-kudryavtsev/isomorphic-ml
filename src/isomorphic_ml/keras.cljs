(ns isomorphic-ml.keras
  (:require
   [tfjs]
   [clojure.core.async :as async :include-macros true]
   [oops.core :refer [oget oset! ocall oapply ocall! oapply!
                      oget+ oset!+ ocall+ oapply+ ocall!+ oapply!+]]))

(defn load-model [url]
  (let [c (async/chan)]
    (ocall ((oget js/window ".tf.loadLayersModel") url) "then"
           (fn [result]
             (async/go (async/>! c result))))
    c))





