(require '[cljs.build.api :as b])

(b/watch "src"
         {:main 'astra.core
          :output-to "resources/public/js/main.js"
          :asset-path "js/"
          :output-dir "resources/public/js"})
