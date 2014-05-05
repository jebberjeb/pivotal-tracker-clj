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

(let [->method {:get client/get
                :put client/put
                :post client/post}]

  (defn request
    [method req url]
    (let [response ((method ->method) url (wrap-request req))]
      (log url req response method)
      response)))

(def POST (partial request :post))
(def PUT (partial request :put))
(def GET (partial request :get))

