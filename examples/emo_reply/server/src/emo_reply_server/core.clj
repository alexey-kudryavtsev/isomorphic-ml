(ns emo-reply-server.core
  (:require [isomorphic-ml.numpy]
            [ring.adapter.jetty :as raj]
            [ring.util.response :as rur]
            [ring.middleware.cors :as rmc]
            [reitit.ring :as reitit-ring]))

(def handler
  (->
   [["/re-train" 
     {:post (fn [_] {:status 200, :body "ok"})}]
    ["/load-model/*file" 
     {:get (fn [{{file :file} :path-params}] 
             (rur/file-response (str "/tmp/emo_server/" file)))}]]
   reitit-ring/router
   reitit-ring/ring-handler
   (rmc/wrap-cors 
    :access-control-allow-origin [#".*"]
    :access-control-allow-methods [:get :post])))

(defn -main [& args]
  (raj/run-jetty handler {:port 3000}))