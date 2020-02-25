(ns isomorphic-ml.python
  (:require [libpython-clj.python :as py]
            [clojure.core.async :as async]))

(defn eval
  "Evaluates one-line string and returns result."  
  [s]
  (let [result (str (gensym "result"))]
    (get-in (py/run-simple-string 
             (str result "=" s))
            [:locals result])))

(defn init []
  (async/go (py/initialize!)
            true))