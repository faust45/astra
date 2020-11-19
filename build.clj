(require '[cljs.build.api :as b])

(b/build "src"
         {:main 'astra.core
          :output-to "resources/public/js/main.js"
          :output-dir "resources/public/js"
          :asset-path "js/"
          :optimizations :simple})
