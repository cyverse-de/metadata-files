(ns org.cyverse.metadata-files.datacite-4-1.subjects
  (:use [medley.core :only [remove-vals]]
        [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The subject element

(defn- build-subject-attributes [subject-scheme scheme-uri value-uri language]
  (->> {:subjectScheme subject-scheme
        :schemeURI     scheme-uri
        :valueURI      value-uri
        ::xml/lang     language}
       (remove-vals string/blank?)))

(defn- get-subject-scheme [_ {:keys [avus]}]
  (util/attr-value avus "subjectScheme"))

(defn- get-scheme-uri [_ {:keys [avus]}]
  (util/attr-value avus "schemeURI"))

(defn- get-value-uri [_ {:keys [avus]}]
  (util/attr-value avus "valueURI"))

(deftype Subject [subject subject-scheme scheme-uri value-uri language]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/subject (build-subject-attributes subject-scheme scheme-uri value-uri language) subject)))

(deftype SubjectGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "subject")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (child-element-factories [_] [])

  (validate [self {subject :value :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location subject)
      (get-subject-scheme self attribute)
      (get-scheme-uri self attribute)
      (get-value-uri self attribute)))

  (get-location [_] (str parent-location ".subject"))

  (generate-nested [self {subject :value avus :avus :as attribute}]
    (let [location (mdf/get-location self)]
      (Subject. subject
                (get-subject-scheme location attribute)
                (get-scheme-uri location attribute)
                (get-value-uri location attribute)
                (util/get-language avus)))))

(defn new-subject-generator [location]
  (SubjectGenerator. location))

;; The subjects element

(defn new-subjects-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-subject-generator]
    :tag                 ::datacite/subjects
    :parent-location     location}))
