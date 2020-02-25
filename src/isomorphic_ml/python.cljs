(ns isomorphic-ml.python
  (:require [pyodide]
            [clojure.core.async :as async :include-macros true]))

(def ^:private python-ready? (async/chan))

(defn eval
  "Evaluates one-line string and returns result."
  [s]
  (js/pyodide.runPython s))

(defn init []
  (let [c (async/chan)]
    (.then js/languagePluginLoader #(async/go (async/>! c true)))
    c))