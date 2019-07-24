package com.bryghts.thecollector
package data

import ops._

final case class Single[+A](a: A) extends TraverseOps[A, Single]{
  def leftHaltingForeach(f: A => Boolean): Unit = f(a)
  def rightHaltingForeach(f: A => Boolean): Unit = f(a)
}

