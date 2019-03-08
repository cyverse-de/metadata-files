(ns org.cyverse.metadata-files.datacite-4-1.name-identifier
  (:use [medley.core :only [remove-vals]]
        [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The nameIdentifier element

(defn- get-name-identifier-scheme [location {:keys [avus]}]
  (util/get-required-attribute-value location avus "nameIdentifierScheme"))

(defn- get-name-identifier-scheme-uri [_ {:keys [avus]}]
  (util/attr-value avus "schemeURI"))

(deftype NameIdentifier [name-identifier scheme scheme-uri]
  mdf/XmlSerializable
  (to-xml [_]
    (let [attrs (remove-vals string/blank? {:nameIdentifierScheme scheme :schemeURI scheme-uri})]
      (element ::datacite/nameIdentifier attrs name-identifier))))

(deftype NameIdentifierGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "nameIdentifier")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (child-element-factories [_] [])
  (get-location [_] (str parent-location ".nameIdentifier"))

  (validate [self {name-identifier :value :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location name-identifier)
      (get-name-identifier-scheme location attribute)
      (get-name-identifier-scheme-uri location attribute)))

  (generate-nested [self {name-identifier :value :as attribute}]
    (let [location (mdf/get-location self)]
      (NameIdentifier. name-identifier
                       (get-name-identifier-scheme location attribute)
                       (get-name-identifier-scheme-uri location attribute)))))

(defn new-name-identifier-generator [location]
  (NameIdentifierGenerator. location))