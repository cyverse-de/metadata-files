(ns org.cyverse.metadata-files.datacite-4-2.rights-list
  (:use [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The rights element

(defn- get-rights-attrs [_ {:keys [avus]}]
  {:rightsURI (util/attr-value avus "rightsURI")
   :rightsIdentifier (util/attr-value avus "rightsIdentifier")
   :rightsIdentifierScheme (util/attr-value avus "rightsIdentifierScheme")
   :schemeURI (util/attr-value avus "schemeURI")})

(defn new-rights-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "rights"
    :min-occurs      0
    :max-occurs      "unbounded"
    :attrs-fn        get-rights-attrs
    :tag             ::datacite/rights
    :parent-location location}))

(defn new-local-contexts-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "LocalContexts"
    :min-occurs      0
    :max-occurs      "unbounded"
    :attrs-fn        get-rights-attrs
    :tag             ::datacite/rights
    :parent-location location}))

;; The rightsList element

(defn new-rights-list-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-rights-generator
                          new-local-contexts-generator]
    :tag                 ::datacite/rightsList
    :parent-location     location}))
