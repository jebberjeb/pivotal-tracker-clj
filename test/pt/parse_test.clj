(ns pt.parse-test
  (:require [clojure.test :refer :all]
            [pt.parse :as pt]))

(deftest render-form
  (testing "rendering forms"
    (let [form1 {:id "4"
                 :name "a story"
                 :url "http://foo.com"
                 :description ""}
          form2 {:id "3"
                 :name "another story"
                 :url "http://bar.com"
                 :description ""}
          delim "==="
          result (str "[id]: 4\n[name]: a story\n[updated_at]: \n"
                      "[descriptionmd5]: \n[description]: \n"
                      "===\n\n"
                      "[id]: 3\n[name]: another story\n[updated_at]: \n"
                      "[descriptionmd5]: \n[description]: \n"
                      "===\n\n")]
      (is (= result (pt/render-forms [form1 form2] delim "\n"))))))

(deftest ast->map
  (testing "converting ast into map"
    (let [ast [:FORM
               [:PAIR
                [:LABEL [:LABEL_TEXT "id"]]
                [:TEXT "1" ]
                [:NEWLINE]]
               [:PAIR
                [:LABEL [:LABEL_TEXT "desc"]]
                [:TEXT "some text"]
                [:NEWLINE]
                [:TEXT "more text"]
                [:NEWLINE]]]
          pair [:PAIR
                [:LABEL [:LABEL_TEXT "desc"]]
                [:TEXT "some text"]
                [:NEWLINE]
                [:TEXT "more text"]
                [:NEWLINE]]]
      (is (= "some text\nmore text\n" (pt/pair->text pair)))
      (is (= {:id "1" :desc "some text\nmore text"}
             (pt/ast->map ast))))))

(deftest extract-data
  (testing "extract data from forms"
    (let [fields [:id :url :description]
          delim #"[=\\*=\\*=\\*=\\*=]"
          forms (str "[id]: 1\n[url]: http://foo.com\n[description]: foo\n"
                     "=*=*=*=*=\n"
                     "[id]: 3\n[url]: http://bar.com\n[description]: fooz\n"
                     "=*=*=*=*=\n\n")
          result [{:id "1" :url "http://foo.com" :description "foo"}
                  {:id "3" :url "http://bar.com" :description "fooz"}]]
      (is (= result (vec (pt/extract-data forms fields delim)))))))

