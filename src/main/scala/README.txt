These files contain the accompanying source code to the paper:

  Christian Hofer, Klaus Ostermann, Tillmann Rendel, Adriaan Moors,
  Polymorphic Embedding of DSLs
  submitted to GPCE 2008

The following packages are included:

------------------------
package polyemb.standard
------------------------

The source code from the paper, supplemented by the omitted parts
- regions: The Regions language
- intermediate: The translation of the Regions language to an arithmetics language
- functions: The Functions language + composition of both languages

The following files contain executable Main objects:
- regions/Main.scala: demonstrating Regions language (incl. translation to arithmetics language)
- functions/FunReg.scala: demonstrating integration of Functions with Regions language


----------------------
package polyemb.fampol
----------------------

The exact analogon to the standard package, but featuring infix syntax implemented via family polymorphism


------------------------
package polyemb.dataflow
------------------------

Simple "available expressions" dataflow analysis as described in Sec. 2.1.1 of the book

  Flemming Nielson, Hanne Riis Nielson, Chris Hankin, 
  Principles of Program Analysis, 
  Springer 2005

in our DSL design


---------------------------------
How to compile and run this code?
---------------------------------

(1) Download and install the Scala compiler from http://www.scala-lang.org/

(2) compile the packages

> scalac -Xexperimental polyemb/standard/functions/*.scala polyemb/standard/intermediate/*.scala polyemb/standard/regions/*.scala
> scalac -Xexperimental polyemb/fampol/functions/*.scala polyemb/fampol/intermediate/*.scala polyemb/fampol/regions/*.scala
> scalac -Xexperimental polyemb/dataflow/*.scala

(3) run the Main objects

> scala polyemb.standard.regions.Main
> scala polyemb.standard.functions.Main
> scala polyemb.fampol.regions.Main
> scala polyemb.fampol.functions.Main
> scala polyemb.dataflow.Main