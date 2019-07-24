package com.bryghts.thecollector
package data

import scala.collection.immutable.{List => SList, Nil => SNil}
import ops._
import FreeStream.Generator
import FreeStream.Iteration

class FreeStream[+A](generator: Generator[A]) extends MonadicOps[A, FreeStream]
                                                 with TraverseLeftOps[A, FreeStream]
                                                 with FilterOps[A, FreeStream]
                                                 with GrowthOps[A, FreeStream] {

  def leftConcat[B >: A, OC[+OB] <: TraverseOps[OB,OC]](that: OC[B]): FreeStream[B] = {
    FreeStream.fromGenerator[B]{(i: Iteration[B]) =>
      val r1 = that.leftHaltingFold(true){(_, b) =>
        val r = i(b)
        (r, r)
      }
      if(r1)
        leftHaltingFold(true){(_, a) =>
          val r = i(a)
          (r, r)
        }
    }
  }

  def rightConcat[B >: A, OC[+OB] <: TraverseOps[OB,OC]](that: OC[B]): FreeStream[B] = {
    FreeStream.fromGenerator[B]{(i: Iteration[B]) =>
      val r1 = leftHaltingFold(true){(_, a) =>
        val r = i(a)
        (r, r)
      }
      if(r1)
        that.leftHaltingFold(true){(_, b) =>
          val r = i(b)
          (r, r)
        }
    }
  }

  protected def genEmpty[B]: FreeStream[B] =
    FreeStream()

  def withFilter(p: (A) => Boolean): FreeStream[A] = {
    FreeStream.fromGenerator[A]{(i: Iteration[A]) =>
      val inner: Iteration[A] = {a =>
        if(p(a)) i(a)
        else true
      }
      generator(inner)
    }
  }

  def flatMap[B, IC[+IB] <: TraverseOps[IB,IC]](f: A => IC[B]): FreeStream[B] =
    FreeStream.fromGenerator[B]{(i: Iteration[B]) =>
      val inner: Iteration[A] = {a =>
        f(a).leftHaltingFold(true){(_, b) =>
          val r = i(b)
          (r, r)
        }
      }
      generator(inner)
    }

  def map[B](f: A => B): FreeStream[B] =
    FreeStream.fromGenerator[B]{(i: Iteration[B]) =>
      val inner: Iteration[A] = {a =>
        i(f(a))
      }
      generator(inner)
    }

  def leftHaltingForeach(f: A => Boolean): Unit = generator(f)
}

object FreeStream {

  type Iteration[A] = A => Boolean
  type Generator[A] = Iteration[A] => Unit

  def apply[A](as: A*): FreeStream[A] = {
    fromGenerator{(f: Iteration[A]) =>
      var continue = true
      val it = as.iterator
      while(continue && it.hasNext) {
        val a = it.next
        continue = f(a)
      }
    }
  }

  def apply[A, C[+X] <: TraverseLeftOps[X, C]](as: C[A]): FreeStream[A] = {
    fromGenerator{(f: Iteration[A]) =>
      as.leftHaltingForeach(f)
    }
  }

  def fromGenerator[A](generator: Generator[A]) =
    new FreeStream(generator)

}
