(ns pt.cli
  (:require [clojure.tools.cli :refer [cli]]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [pt.api :as a]
            [pt.parse :as p]
            [digest :refer [md5]])
  (:gen-class))

(defn- parse-args
  [args]
  (cli args
       ["-f" "--file" "File which (will) contains stories" :default
        "/tmp/stories.txt"]
       ["-q" "--query" "Search query for fetching stories" :default
        "foozle"]
       ["-l" "--load-stories" "Load all stories" :default false :flag true]
       ["-s" "--save-story" "Save a story by id from the file" :default nil]
       ["-t" "--token" "The pivotal tracker access token"]
       ["-p" "--project-id" "The id of the project to query"]
       ["-?" "--help" "Show this help" :default false :flag true]))

(defn backup-file
  [file]
  (let [f (io/file file)
        bf (io/file (str file (System/currentTimeMillis)))]
    (if (.exists f) (io/copy f bf))))

(defn write-stories!
  [stories delim file]
  (spit file (p/render-forms stories delim "\n")))

(defn load-stories!
  [file delim token project-id query]
  (-> (a/search-stories token project-id query)
      (write-stories! delim file)))

(defn load-stories-by-ids!
  [ids file delim token project-id]
  (write-stories! (map (partial a/get-story token project-id) ids) delim file))

(defn description-modified?
  [story]
  (let [new-md5 (md5 (:description story))
        old-md5 (:descriptionmd5 story)]
    (not= new-md5 old-md5)))

(defn sync-stories-desc!
  [file render-delim parse-delim token project-id backup-file? story-id]
  (if backup-file?
    (backup-file file))
  (let [stories (p/extract-data (slurp file) [:id :name :updated_at
                                              :description :descriptionmd5]
                                parse-delim)
        ids (map :id stories)]
    (->> stories
         (filter #(or (nil? story-id) (= (:id %) story-id)))
         (filter description-modified?)
         ((fn [stories]
            (log/info "Updating stories: " (map :id stories))
            stories))
         (map (partial a/update-story-description token project-id))
         (doall))
    ;; TODO - do we need to reload them all in the case where we've only
    ;; saved one?
    (load-stories-by-ids! ids file render-delim token project-id)))

(defn -main
  [& args]
  (let [render-delim "=*=*=*=*=*=*="
        parse-delim #"=\*=\*=\*=\*=\*=\*="
        [options args banner] (parse-args args)
        {:keys [file save-story help load-stories query token project-id]}
        options]
    (cond help (println banner)
          load-stories (load-stories! file render-delim token project-id query)
          save-story (sync-stories-desc! file render-delim parse-delim token
                                   project-id true save-story))))

