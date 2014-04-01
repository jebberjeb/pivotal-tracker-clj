(ns pt.http
  (:require [clojure.tools.logging :as log]
            [clj-http.client :as client]
            [clojure.string :refer [upper-case]]))

(defn log
  [url request response method]
  (log/info "***HTTP " (upper-case (name method)) "*** : " url)
  ;(log/info "request: " request)
  ;(log/info "response: " response)
  )

(defn unescape-html
  [s]
  (-> s
      (.replace "&quot;" "\"")
      (.replace "&amp;"  "&")
      (.replace "&lt;"   "<")
      (.replace "&gt;"   ">")))

(defn wrap-request
  [request & kvs]
  (apply assoc request
         :throw-entire-message? true
         :insecure? true
         kvs))

(defn HTTP
  [method request url]
  (let [response (-> request
                     (wrap-request :url url :method method)
                     client/request)]
    (log url request response method)
    response))

(def POST (partial HTTP :post))
(def PUT  (partial HTTP :put))
(def GET  (partial HTTP :get))
