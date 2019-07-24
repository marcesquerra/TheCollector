package com.bryghts.thecollector
package ops

trait FilterOps[+A, C[+X]] {self: C[A] =>
  def withFilter(p: (A) => Boolean): C[A]

  final def filter(p: A => Boolean): C[A] = withFilter(p)
  final def filterNot(p: A => Boolean): C[A] =
    withFilter(a => !p(a))
}

