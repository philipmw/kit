(ns kit-generator.injections
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer :all]
    [kit-generator.io :refer [delete-folder]]
    [kit.generator.io :as io]
    [kit.generator.modules.injections :refer :all]))

(def config-template "test/resources/sample-system.edn")
(def test-config "test/resources/generated/system.edn")

(deftest injection-test
  (when (.exists (io/file test-config))
    (io/delete-file test-config))
  (spit test-config (slurp config-template))
  (is
    (=
      "{:system/env #profile {:dev :dev, :test :test, :prod :prod}, :server/undertow {:port #long #or [#env PORT 3000], :handler #ig/ref :handler/ring}, :handler/ring {:router #ig/ref :router/core, :api-path \"/api\"}, :reitit.routes/api {:base-path \"/api\", :env #ig/ref :system/env}, :router/routes {:routes #ig/refset :reitit/routes}, :router/core {:routes #ig/ref :router/routes}, :foo :bar}
"
      (io/edn->str
        (inject {:type   :edn
                 :target (io/str->edn (slurp "test/resources/generated/system.edn"))
                 :query  []
                 :action :merge
                 :value  {:foo :bar}})))))