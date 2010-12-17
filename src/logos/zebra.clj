(ns logos.zebra
  (:refer-clojure :exclude [reify inc == take])
  (:use logos.minikanren
        logos.logic)
  (:require [clojure.contrib.macro-utils :as macro]))

(defn on-right-o [x y l]
  (exist [r]
   (cond-e
    ((== (llist x y r) l))
    ((rest-o l r)
     (on-right-o x y r)))))

(defn next-to-o [x y l]
  (cond-e
   ((on-right-o x y l))
   ((on-right-o y x l))))

(defn zebra [hs]
  (macro/symbol-macrolet [_ (lvar)]
   (all
    (== [_ _ [_ _ 'milk _ _] _ _] hs)                         ;; there are five houses, the person that lives in the middle drinks milk
    (first-o hs ['norwegian _ _ _ _])                         ;; the norwegian lives in the first house
    (next-to-o ['norwegian _ _ _ _] [_ _ _ _ 'blue] hs)       ;; the norwegian lives next to the blue horse
    (on-right-o [_ _ _ _ 'ivory] [_ _ _ _ 'green] hs)         ;; green house is right of the ivory house
    (member-o ['englishman _ _ _ 'red] hs)                    ;; the englishman lives in the red house
    (member-o [_ 'kools _ _ 'yellow] hs)                      ;; kools are smoked in the yellow house
    (member-o ['spaniard _ _ 'dog _] hs)                      ;; the spaniard has a dog
    (member-o [_ _ 'coffee _ 'green] hs)                      ;; coffee is drunk in the green house
    (member-o ['ukrainian _ 'tea _ _] hs)                     ;; the ukrainian drinks tea
    (member-o [_ 'lucky-strikes 'oj _ _] hs)                  ;; the lucky strikes smoker drinks oj
    (member-o ['japanese 'parliaments _ _ _] hs)              ;; the japanese man smokes parliaments
    (member-o [_ 'oldgolds _ 'snails _] hs)                   ;; old gold smoker owns snails
    (next-to-o [_ _ _ 'horse _] [_ 'kools _ _ _] hs)          ;; kools are smoked in the house next to the horse
    (next-to-o [_ _ _ 'fox _] [_ 'chesterfields _ _ _] hs)))) ;; the man who smokes chesterfields is next to the the man who owns a fox

;; different ordering
;; (defn zebra [hs]
;;   (macro/symbol-macrolet [_ (lvar)]
;;    (all
;;     (== [_ _ [_ _ 'milk _ _] _ _] hs)
;;     (first-o hs ['norwegian _ _ _ _])
;;     (next-to-o ['norwegian _ _ _ _] [_ _ _ _ 'blue] hs)
;;     (next-to-o [_ _ _ 'horse _] [_ 'kools _ _ _] hs)
;;     (next-to-o [_ _ _ 'fox _] [_ 'chesterfields _ _ _] hs)
;;     (on-right-o [_ _ _ _ 'ivory] [_ _ _ _ 'green] hs)
;;     (member-o ['englishman _ _ _ 'red] hs)
;;     (member-o [_ 'kools _ _ 'yellow] hs)
;;     (member-o [_ _ 'coffee _ 'green] hs)
;;     (member-o [_ 'oldgolds _ 'snails _] hs)
;;     (member-o ['spaniard _ _ 'dog _] hs)
;;     (member-o ['ukrainian _ 'tea _ _] hs)
;;     (member-o [_ 'lucky-strikes 'oj _ _] hs)
;;     (member-o ['japanese 'parliaments _ _ _] hs))))

(defn zebra-o []
  (run* [q]
        (zebra q)))

(comment
  ;; FAIL: same lazy-seq complaint as flatten
  (zebra-o)

  ;; < 14-20ms now, but still not that fast
  ;; compared to Chez Scheme + SBRALs 2.4-2.8s, that means 2ms
  ;; slowest walk is 4.6s means 4ms
  ;; so that 5-10X faster than what we have
  ;; member version takes longer!

  ;; very, very, very interesting
  ;; zebra is no slower under lazy
  ;; no stackoverflow error on orderings!
  
  (dotimes [_ 10]
    (time
     (let  [a (zebra-o)]
       (dotimes [_ 1]
         (doall a)))))

  ;; succeeds twice
  (run* [q]
        (on-right-o 'cat 'dog '[cat dog cat dog]))

  ;; succeed once
  (run* [q]
        (on-right-o 'dog 'cat '[cat dog cat dog]))

  ;; succeed three times
  (run* [q]
        (next-to-o 'cat 'dog '[cat dog cat dog]))
  )