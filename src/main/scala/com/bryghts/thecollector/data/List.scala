package com.bryghts.thecollector
package data

import ops._

sealed trait List[+A] extends MonadicOps[A, List]
                         with FilterOps[A, List]
                         with TraverseOps[A, List]{

  def reverse: List[A]
  def isEmpty = this == Nil

}

case object Nil extends List[Nothing] {
  def flatMap[B](f: Nothing => List[B]): List[B] = Nil
  def map[B](f: Nothing => B): List[B] = Nil
  def withFilter(p: Nothing => Boolean): List[Nothing] = Nil

  def reverse: List[Nothing] = Nil
  def leftForeach[R](f: Nothing => R): Unit = ()
  def rightForeach[R](f: Nothing => R): Unit = ()
}

case class NonEmptyList[+A] (head: A, tail: List[A]) extends List[A] {
  def flatMap[B](f: A => List[B]): List[B] = {
    var result: List[B] = Nil
    rightForeach{a =>
      val bs = f(a)
      bs.rightForeach{b => result = NonEmptyList(b, result)}
    }
    result
  }
  def map[B](f: A => B): List[B] = NonEmptyList(f(head), tail.map(f))
  def withFilter(p: A => Boolean): List[A] =
    if(p(head)) NonEmptyList (head, tail.withFilter(p))
    else tail.withFilter(p)

  def reverse: List[A] = {
    var result: List[A] = Nil
    this.leftForeach{a => result = NonEmptyList(a, result)}
    result
  }

  def leftForeach[R](f: A => R): Unit = {
    var these: NonEmptyList[A] = this
    while (true) {
      f(these.head)
      these.tail match {
        case Nil => return
        case other : NonEmptyList[A] => these = other
      }
    }
  }

  def rightForeach[R](f: A => R): Unit =
    reverse.foreach(f)
}
