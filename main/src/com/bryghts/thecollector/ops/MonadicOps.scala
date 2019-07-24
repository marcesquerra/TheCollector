package com.bryghts.thecollector
package ops


trait MonadicOps[+A, C[+X] <: MonadicOps[X, C]] {self : C[A] =>
  def map[B](f: A => B): C[B]
  def flatMap[B, IC[+IB] <: TraverseOps[IB, IC]](f: A => IC[B]): C[B]

  final def flatten[B, IC[+IB] <: TraverseOps[IB, IC]](implicit ev: A <:< IC[B]): C[B] =
    flatMap[B, IC](identity)
}
