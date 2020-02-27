(defproject emo-reply-example "0.0.1-SNAPSHOT"
  :description "Example project: emotion recognition with Clojure."
  :license {:name "MIT License"
            :distribution :repo}

  :min-lein-version "2.3.4"

  :source-paths ["src"]
  
  :main emo-reply-server.core

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.597"]
                 [ring "1.8.0"]
                 [ring-cors "0.1.13"]
                 [metosin/reitit "0.4.2"]
                 [isomorphic-ml "0.0.1-SNAPSHOT"]]
  )