---
layout: doc-page
title: "Algebraic Data Types"
---


The `enum` concept is general enough to also support algebraic data
types (ADTs) and their generalized version (GADTs). Here's an example
how an `Option` type can be represented as an ADT:

```scala
enum Option[+T] {
  case Some[+T](x: T)
  case None
}
```

This example introduces `Option` enum class with a covariant type
parameter `T`, together with two cases, `Some` and `None`. `Some` is
parameterized with a type parameter `T` and a value parameter `x`. It
is a shorthand for writing a case class that extends `Option`. Since
`None` is not parameterized it is treated as a normal enum value.

The `extends` clauses that were omitted in the example above can also
be given explicitly:

```scala
enum Option[+T] {
  case Some[+T](x: T) extends Option[T]
  case None           extends Option[Nothing]
}
```

Note that the parent type of `None` is inferred as
`Option[Nothing]`. Generally, all covariant type parameters of the enum
class are minimized in a compiler-generated extends clause whereas all
contravariant type parameters are maximized. If `Option` was non-variant,
you'd need to give the extends clause of `None` explicitly.

As for normal enum values, the cases of an `enum` are all defined in
the `enum`s companion object. So it's `Option.Some` and `Option.None`
unless the definitions are "pulled out" with an import:

```scala
scala> Option.Some("hello")
val res1: t2.Option[String] = Some(hello)
scala> Option.None
val res2: t2.Option[Nothing] = None
```

Note that the type of the expressions above is always `Option`. That
is, the implementation case classes are not visible in the result
types of their `apply` methods. This is a subtle difference with
respect to normal case classes. The classes making up the cases do
exist, and can be unvealed by constructing them directly with a `new`.

```scala
val res3: t2.Option.Some[Int] = Some(2)
scala> scala> new Option.Some(2)
```

As all other enums, ADTs can have methods on both the enum class and
its companion object. For instance, here is `Option` again, with an
`isDefined` method and an `Option(...)` constructor.

```scala
enum class Option[+T] {
   def isDefined: Boolean
}
object Option {
  def apply[T >: Null](x: T): Option[T] =
    if (x == null) None else Some(x)

  case Some[+T](x: T) {
     def isDefined = true
  }
  case None {
     def isDefined = false
  }
}
```

Enumerations and ADTs have been presented as two different
concepts. But since they share the same syntactic construct, they can
be seen simply as two ends of a spectrum and it is perfectly possible
to conctruct hybrids. For instance, the code below gives an
implementation of `Color` either with three enum values or with a
parameterized case that takes an RGB value.

```scala
enum Color(val rgb: Int) {
  case Red   extends Color(0xFF0000)
  case Green extends Color(0x00FF00)
  case Blue  extends Color(0x0000FF)
  case Mix(mix: Int) extends Color(mix)
}
```

## Syntax of Enums

Changes to the syntax fall in two categories: enum classes and cases inside enums.
The changes are specified below as deltas with respect to the Scala syntax given [here](https://github.com/lampepfl/dotty/blob/master/docs/docs/internals/syntax.md)

 1. Enum definitions and enum classes are defined as follows:

        TmplDef ::=  `enum' `class’ ClassDef
                 |   `enum' EnumDef
        EnumDef ::=  id ClassConstr [`extends' [ConstrApps]]
                     [nl] `{’ EnumCaseStat {semi EnumCaseStat} `}’

2. Cases of enums are defined as follows:

        EnumCaseStat  ::=  {Annotation [nl]} {Modifier} EnumCase
        EnumCase      ::=  `case' (EnumClassDef | ObjectDef | ids)
        EnumClassDef  ::=  id [ClsTpeParamClause | ClsParamClause]
                           ClsParamClauses TemplateOpt
        TemplateStat  ::=  ... | EnumCaseStat





