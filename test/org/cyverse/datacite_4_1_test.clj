(ns org.cyverse.datacite-4-1-test
  (:require [clojure.data.xml :refer :all]
            [clojure.string :as string]
            [clojure.test :refer :all]
            [org.cyverse.metadata-files :refer :all]
            [org.cyverse.metadata-files.datacite-4-1 :refer [build-datacite]]
            [org.cyverse.metadata-files.test-utils :refer [test-xml]])
  (:import [java.util.regex Pattern]))

(def ^:private min-attrs
  [{:attr "identifier" :value "   doi:10.1000/182" :avus [{:attr "identifierType" :value "DOI"}]}
   {:attr "creator" :value "the-creator"}
   {:attr "title" :value "the-title"}
   {:attr "publisher" :value "CyVerse"}
   {:attr "publicationYear" :value "2019"}
   {:attr "resourceType" :value "XML" :avus [{:attr "resourceTypeGeneral" :value "Dataset"}]}])

(def ^:private schema-url
  "http://schema.datacite.org/meta/kernel-4.1/metadata.xsd")

(defn- test-datacite [file attrs]
  (test-xml file (build-datacite attrs) schema-url))

(defn- debug-datacite [file attrs]
  (test-xml file (build-datacite attrs) schema-url true))

(defn- test-missing-element-attributes [attrs]
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        (re-pattern "Metadata validation failed.")
                        (build-datacite attrs))))

(defn- test-missing-attribute [attrs]
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        (re-pattern "Missing required attribute.")
                        (build-datacite attrs))))

(deftest test-minimal
  (testing "Minimal DataCite file."
    (test-datacite "datacite-4.1/minimal.xml" min-attrs)))

(deftest test-missing-top-level-element-attributes
  (testing "Missing top-level attributes."
    (doseq [attr-name (mapv :attr min-attrs)]
      (test-missing-element-attributes (remove (comp (partial = attr-name) :attr) min-attrs)))))
