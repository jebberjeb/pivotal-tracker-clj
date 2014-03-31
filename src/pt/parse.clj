(ns pt.parse
  (:require [clojure.string :as st]
            [clojure.tools.logging :as log]
            [instaparse.core :as insta]
            [schema.core :as s]
            [schema.macros :as sm]
            [clostache.parser :as c]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; SCHEMA

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; HELPER

(defn field-nm [field] (name (first field)))
(defn field-val [field] (second field))

(def rules "
           FORM = <WHITESPACE> PAIR+
           PAIR = LABEL (TEXT NEWLINE)+
           TEXT = #'.*'
           LABEL = <'['> LABEL_TEXT <']'> <': '>
           LABEL_TEXT = %s
           NEWLINE = '\n'
           WHITESPACE = #'\\s*'
           ")

;; TODO
;;  - try command line against stories-hangs.txt

(defn mk-parser [rules fieldlist]
  (insta/parser (format rules (->> fieldlist
                                   (map name)
                                   (map #(str "'" % "'"))
                                   (st/join " | ")))))

(defn pair->label
  [pair-node]
  (-> pair-node second second second keyword))

(defn pair->text
  [pair-node]
  (->> pair-node
       (drop 2)
       (reduce (fn [s e]
                 (str s (condp = (first e)
                          :TEXT (second e)
                          :NEWLINE "\n"
                          (throw (ex-info "TEXT or NEWLINE expected"))))) "")))

(defn ast->map
  [form-node]
  (->> form-node
       (rest)
       (filter (comp (partial = :PAIR) first))
       (map (juxt pair->label (comp st/trim pair->text)))
       (into {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; PUBLIC

(def new-form {:id nil
               :name nil
               :updated_at nil
               :description nil
               :descriptionmd5 nil})

(sm/defn ^:always-validate render-forms :- s/Str
  [forms :- [{s/Keyword s/Any}]
   delimiter :- s/Str
   nl :- s/Str]
  (->> forms
       (map (partial merge {:delimiter delimiter}))
       (hash-map :forms)
       (c/render-resource "stories.mustache")))

(sm/defn ^:always-validate extract-data :- [{s/Keyword s/Any}]
  [forms :- s/Str
   field-list :- [s/Keyword]
   delimiter :- s/Any]
  (let [parser (mk-parser rules field-list)
        pieces (filter (comp not empty? (partial st/trim))
                       (st/split forms delimiter))
        asts (map parser pieces)
        maps (map ast->map asts)]
    (log/info (format "extract-data: %s stories found " (count maps)))
    maps))

