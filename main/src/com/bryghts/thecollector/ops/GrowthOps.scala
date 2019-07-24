package com.bryghts.thecollector
package ops

import data.Single

trait GrowthRightOps[+A, C[+X] <: GrowthRightOps[X, C]] {self: C[A] =>

  protected def genEmpty[B]: C[B]

  def rightConcat[B >: A, OC[+OB] <: TraverseOps[OB, OC]](that: OC[B]): C[B]

  def rightAdd[B >: A](b: B): C[B] =
    rightConcat(Single(b))

  final def ++[B >: A, OC[+OB] <: TraverseOps[OB, OC]](that: OC[B]): C[B] =
    rightConcat(that)

  final def :+ [B >: A](b: B): C[B] =
    rightAdd(b)

}

trait GrowthLeftOps[+A, C[+X] <: GrowthLeftOps[X, C]] {self: C[A] =>

  protected def genEmpty[B]: C[B]

  def leftConcat[B >: A, OC[+OB] <: TraverseOps[OB, OC]](that: OC[B]): C[B]

  def leftAdd[B >: A](b: B): C[B] =
    leftConcat(Single(b))

  final def ++:[B >: A, OC[+OB] <: TraverseOps[OB, OC]](that: OC[B]): C[B] =
    leftConcat(that)

  final def +: [B >: A](b: B): C[B] =
    leftAdd(b)

}

trait GrowthOps[+A, C[+X] <: GrowthOps[X, C]] extends GrowthRightOps[A, C] with GrowthLeftOps[A, C] {self: C[A] =>
}

trait GrowAndTraverseOps[+A, C[+X] <: GrowAndTraverseOps[X, C]] extends GrowthOps[A, C] with TraverseOps[A, C] {self: C[A] =>

  final def reverseLeftGrowRightTraverse: C[A] = {
    var r = genEmpty[A]
    rightForeach{a => a +: r}
    r
  }

  final def reverseRightGrowLeftTraverse: C[A] = {
    var r = genEmpty[A]
    leftForeach{a => r :+ a}
    r
  }

  def reverse: C[A] = reverseRightGrowLeftTraverse
}
