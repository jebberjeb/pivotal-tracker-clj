(ns ^:integration pt.http-test
  (:require [clojure.test :refer :all]
            [pt.http :as http]))

(deftest http-request
  (testing "HTTP request"
    (is (= 200 (:status (http/GET {} "https://www.google.com/"))))))
