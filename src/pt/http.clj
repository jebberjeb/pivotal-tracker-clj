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
  [request]
  (assoc request
         :throw-entire-message? true
         :insecure? true))

(defn POST
  [request url]
  (let [response (client/post url (wrap-request request))]
    (log url request response :post)
    response))

(defn PUT
  [request url]
  (let [response (client/put url (wrap-request request))]
    (log url request response :put)
    response))

(defn GET
  [request url]
  (let [response (client/get url (wrap-request request))]
    (log url request response :get)
    response))
