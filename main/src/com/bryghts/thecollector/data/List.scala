package com.bryghts.thecollector
package data

import ops._

sealed trait List[+A] extends MonadicOps[A, List]
                         with FilterOps[A, List]
                         with TraverseOps[A, List]
                         with GrowthOps[A, List]
                         with GrowAndTraverseOps[A, List] {

  final override protected def genEmpty[B]: List[B] = Nil
  final override def reverse: List[A] = reverseLeftGrowRightTraverse
  override final def isEmpty = this == Nil

  override final def leftConcat[B >: A, OC[+OB] <: TraverseOps[OB,OC]](that: OC[B]): List[B] = {
    var r: List[B] = this

    that.rightForeach{b => r = NonEmptyList(b, r)}

    r
  }

  override final def rightConcat[B >: A, OC[+OB] <: TraverseOps[OB,OC]](that: OC[B]): List[B] = {
    var r: List[B] = Nil

    that.rightForeach{b => r = NonEmptyList(b, r)}

    this.rightForeach{a => r = NonEmptyList(a, r)}
    r
  }

  override final def rightAdd[B >: A](b: B): List[B] = {
    var r: List[B] = NonEmptyList(b, Nil)

    rightForeach(a => r = NonEmptyList(a, r))
    r
  }

  override final def leftAdd[B >: A](b: B): List[B] = NonEmptyList(b, this)

}

case object Nil extends List[Nothing] {
  override final def flatMap[B, IC[+IB] <: TraverseOps[IB, IC]](f: Nothing => IC[B]): List[B] = Nil
  override final def map[B](f: Nothing => B): List[B] = Nil
  override final def withFilter(p: Nothing => Boolean): List[Nothing] = Nil

  override final def leftHaltingForeach(f: Nothing => Boolean): Unit = ()
  override final def rightHaltingForeach(f: Nothing => Boolean): Unit = ()
}

case class NonEmptyList[+A] (head: A, tail: List[A]) extends List[A] {
  override final def flatMap[B, IC[+IB] <: TraverseOps[IB, IC]](f: A => IC[B]): List[B] = {
    var result: List[B] = Nil
    rightForeach{a =>
      val bs = f(a)
      bs.rightForeach{b => result = NonEmptyList(b, result)}
    }
    result
  }
  override final def map[B](f: A => B): List[B] = NonEmptyList(f(head), tail.map(f))
  override final def withFilter(p: A => Boolean): List[A] =
    if(p(head)) NonEmptyList (head, tail.withFilter(p))
    else tail.withFilter(p)

  override final def leftHaltingForeach(f: A => Boolean): Unit = {
    var these: NonEmptyList[A] = this
    while (f(these.head)) {
      these.tail match {
        case Nil => return
        case other : NonEmptyList[A] => these = other
      }
    }
  }

  override final def rightHaltingForeach(f: A => Boolean): Unit =
    reverse.leftForeach(f)
}
