(ns isomorphic-ml.keras
  (:require [libpython-clj.python :as py]
            [libpython-clj.require :as py-req]))

(py/initialize!)

(py-req/require-python '[tensorflow :as tf])

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

(defn add-layer! [model layer]
  (py/$a model add layer))