package com.bryghts.thecollector

trait TraverseOps[A, C[_]] { self: C[A] =>
  def foreach[R](f: A => R): Unit
}

trait MonadicOps[A, C[_]] { self: C[A] =>
  def map[B](f: A => B): C[B]
  def flatMap[B](f: A => C[B]): C[B]
  def withFilter(p: A => Boolean): C[A]

  final def flatten[B](implicit ev: A <:< C[B]): C[B] =
    flatMap[B](identity)
  final def filter(p: A => Boolean): C[A] = withFilter(p)
  final def filterNot(p: A => Boolean): C[A] =
    withFilter(a => !p(a))
}

trait MonadicTraverseOps[A, C[_]] extends TraverseOps[A, C] with MonadicOps[A, C] {
  self: C[A] =>

  override final def foreach[R](f: A => R): Unit = {
    map(f)
    ()
  }
}

trait CombinationOps[A, C[A]] { self: C[A] =>
  def addRight(a: A): C[A]
  def concatRight(as: C[A]): C[A]
}
