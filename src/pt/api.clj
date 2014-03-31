(ns pt.api
  (:require [cheshire.core :as cc]
            [digest :refer [md5]]
            [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [clj-time.core :as t]
            [hiccup.util :as hic]
            [pt.http :as http]))

(def root-url "https://www.pivotaltracker.com/services/v5")

(defn throw-if-not-200
  [response]
  (if-not (= 200 (:status response))
    (throw (ex-info "response not 200" {:response response}))
    response))

(defn add-md5s
  [story]
  (assoc story :descriptionmd5 (md5 (get story :description ""))))

(defn unescape-desc
  [story]
  (let [escaped-story (update-in story [:description] http/unescape-html)]
    (log/info "old story: " story)
    (log/info "new story: " escaped-story)
    story))

(defn get-base-req
  [token]
  {:headers {"X-TrackerToken" token}})

;; TODO - reduce/remove duplication in
;;        * results parsing pipeline
;;        * rest url creation

(defn search-stories
  [token project-id query]
  (let [enc-query (hic/url-encode query)]
    (-> (str root-url "/projects/" project-id "/search?query=" enc-query)
        (->> (http/GET (get-base-req token)))
        (throw-if-not-200)
        (:body)
        (cc/parse-string true)
        (:stories)
        (:stories)
        (->> (map add-md5s)))))

(defn get-story
  [token project-id story-id]
  (-> (str root-url "/projects/" project-id "/stories/" story-id)
      (->> (http/GET (get-base-req token)))
      (throw-if-not-200)
      (:body)
      (cc/parse-string true)
      (add-md5s)))

;; TODO - does this belong in this ns?
(defn story-updated-since?
  "Has the story been updated since (presumably) we originally fetched it?"
  [token project-id story-id updated-at]
  (let [story (get-story token project-id story-id)
        formatter (f/formatters :date-time-no-ms)
        orig-update (f/parse formatter updated-at)
        curr-update (f/parse formatter (:updated_at story))]
    (t/after? curr-update orig-update)))

(defn update-story-description
  [token project-id story]
  (let [url (str root-url "/projects/" project-id "/stories/" (:id story))
        body (select-keys story [:description])
        request (assoc (get-base-req token) :form-params body)
        modified? (story-updated-since? token project-id (:id story)
                                        (:updated_at story))]
    (if modified? (throw (ex-info (format "story %s modified remotely"
                                          (:id story)) story))
      (-> (http/PUT request url)
          (throw-if-not-200)
          (:body)
          (cc/parse-string true)
          ;; Should we be adding md5s here? Does anyone use this response?
          (add-md5s)))))

