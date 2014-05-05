(ns pt.api
  (:require [cheshire.core :as cc]
            [digest :refer [md5]]
            [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [clj-time.core :as t]
            [hiccup.util :as hic]
            [pt.http :as http]))

(def root-url "https://www.pivotaltracker.com/services/v5")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; PRIVATE

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

(defn project-uri
  [root-uri
   project-id]
  (format "%s/projects/%s" root-uri project-id))

(defn story-uri
  [root-uri
   project-id
   story-id]
  (format "%s/stories/%s" (project-uri root-uri project-id) story-id))

(defn comment-uri
  [root-uri
   project-id
   story-id]
  (format "%s/comments" (story-uri root-uri project-id story-id)))

(defn get-base-req
  [token]
  {:headers {"X-TrackerToken" token}})

(defn request
  [method req url]
  ;; pre method is :get | :post | :put
  (-> (http/request method req url)
      (throw-if-not-200)
      (:body)
      (cc/parse-string true)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; PUBLIC

(defn search-stories
  [token project-id query]
  (let [enc-query (hic/url-encode query)
        url (str root-url "/projects/" project-id "/search?query=" enc-query)
        req (get-base-req token)]
    (->> (request :get req url)
         (:stories)
         (:stories)
         (map add-md5s))))

(defn get-story
  ([token project-id story-id]
   (let [url (story-uri root-url project-id story-id)
         req (get-base-req token)]
     (->> (request :get req url)
          (add-md5s))))
  ([token story]
   (get-story token (:project_id story) (:id story))))

;; TODO - does this belong in this ns?
(defn story-updated-since?
  "Has the story been updated since (presumably) we originally fetched it?"
  [token project-id story-id updated-at]
  (let [story (get-story token project-id story-id)
        formatter (f/formatters :date-time-no-ms)
        orig-update (f/parse formatter updated-at)
        curr-update (f/parse formatter (:updated_at story))]
    (t/after? curr-update orig-update)))

(defn update-story
  [field token project-id {:keys [story-id updated_at] :as story}]
  (let [url (story-uri root-url project-id story-id)
        body (select-keys story [field])
        req (assoc (get-base-req token) :form-params body)
        modified? (story-updated-since? token project-id story-id
                                        (:updated_at story))]
    (if modified? (throw (ex-info (format "story %s modified remotely"
                                          story-id) story))
      (request :put req url))))

(def update-story-description (partial update-story :description))

(defn get-comments
  ([token project-id story-id]
   (request :get
            (get-base-req token)
            (comment-uri root-url project-id story-id)))
  ([token story]
   (get-comments token (:project_id story) (:id story))))

(defn add-comment
  [token project-id story-id comment]
  ;; TODO make an arity of one, using only a map
  ;; TODO should share code w/ update-story-description
  "PUT" "/projects/project-id/stories/story-id" "{\"comment\": \"foo bar\""
  )

