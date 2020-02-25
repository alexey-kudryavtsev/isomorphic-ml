(ns emo-reply-client.core
  (:require [isomorphic-ml.python :as py]
            [clojure.core.async :as async :include-macros true]))


(defn main [] 
  (js/console.log "Just printing something to check that pyodide is working")
  (js/console.log (py/eval "11*12")))

(async/go
  (async/<! (py/init))
  (main))