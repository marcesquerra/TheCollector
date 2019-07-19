package com.bryghts.thecollector
package ops


trait MonadicOps[+A, C[+X]] {self : C[A] =>
  def map[B](f: A => B): C[B]
  def flatMap[B](f: A => C[B]): C[B]

  final def flatten[B](implicit ev: A <:< C[B]): C[B] =
    flatMap[B](identity)
}

// trait TraverseOps[A, C[_]] { self: C[A] =>
//   def foreach[R](f: A => R): Unit

//   def fold[R](z: R)(op: (A, R) => R
// }

// trait CombinationOps[A, C[A]] { self: C[A] =>
//   def addRight(a: A): C[A]
//   def concatRight(as: C[A]): C[A]
// }
