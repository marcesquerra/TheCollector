package com.bryghts.thecollector
package ops

trait TraverseRightOps[+A, C[+X] <: TraverseRightOps[X, C]] {self: C[A] =>
  def rightHaltingForeach(f: A => Boolean): Unit

  def rightHead: Option[A] = {
    var r: Option[A] = None
    rightHaltingForeach{a =>
      r = Some(a)
      false
    }
    r
  }

  final def rightForeach[R](f: A => R): Unit = rightHaltingForeach{a => f(a); true}

  final def rightHaltingFold[R](z: R)(op: (A, R) => (R, Boolean)) = {
    var result = z
    this rightHaltingForeach {x =>
      val (r, cont) = op(x, result)
      result = r
      cont
    }
    result
  }

  final def rightFold[R](z: R)(op: (A, R) => R) =
    rightHaltingFold(z){(a, accum) => val r = op(a, accum); (r, true)}

  def isEmpty: Boolean = {
    var r = true
    rightHaltingForeach{_ => r = false; false}
    r
  }

  def nonEmpty: Boolean = !isEmpty
}

trait TraverseLeftOps[+A, C[+X] <: TraverseLeftOps[X, C]] {self: C[A] =>
  def leftHaltingForeach(f: A => Boolean): Unit

  final def leftForeach[R](f: A => R): Unit = leftHaltingForeach{a => f(a); true}

  def foreach[R](f: A => R): Unit = leftForeach(f)

  def leftHead: Option[A] = {
    var r: Option[A] = None
    leftHaltingForeach{a =>
      r = Some(a)
      false
    }
    r
  }

  final def leftHaltingFold[R](z: R)(op: (R, A) => (R, Boolean)) = {
    var result = z
    this leftHaltingForeach {x =>
      val (r, cont) = op(result, x)
      result = r
      cont
    }
    result
  }

  final def leftFold[R](z: R)(op: (A, R) => R) =
    leftHaltingFold(z){(a, accum) => val r = op(accum, a); (r, true)}

  def isEmpty: Boolean = {
    var r = true
    leftHaltingForeach{_ => r = false; false}
    r
  }

  def nonEmpty: Boolean = !isEmpty
}

trait TraverseOps[+A, C[+X] <: TraverseOps[X, C]] extends TraverseRightOps[A, C] with TraverseLeftOps[A, C] {self: C[A] =>
  override def isEmpty: Boolean = super[TraverseLeftOps].isEmpty
  override def nonEmpty: Boolean = super[TraverseLeftOps].nonEmpty
}

