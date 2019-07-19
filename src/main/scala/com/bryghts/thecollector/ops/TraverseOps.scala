package com.bryghts.thecollector
package ops

trait TraverseRightOps[+A, C[+X]] {self: C[A] =>
  def rightForeach[R](f: A => R): Unit
}

trait TraverseLeftOps[+A, C[+X]] {self: C[A] =>
  def leftForeach[R](f: A => R): Unit

  def foreach[R](f: A => R): Unit = leftForeach(f)
}

trait TraverseOps[+A, C[+X]] extends TraverseRightOps[A, C] with TraverseLeftOps[A, C] {self: C[A] =>
}
