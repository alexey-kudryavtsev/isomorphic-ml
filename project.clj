(defproject isomorphic-ml "0.0.1-SNAPSHOT"
  :description "Numpy & Tensorflow bindings for both Clojure and Clojurescript"
  :url "https://github.com/alexey-kudryavtsev/isomorphic-ml"
  :license {:name "MIT License"
            :distribution :repo}

  :min-lein-version "2.3.4"

  :source-paths ["src"]

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.597"]
                 [clj-python/libpython-clj "1.36"]]
)