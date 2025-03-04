/*
 * Copyright (c) 2015 Typelevel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cats

import cats.kernel.CommutativeMonoid
import simulacrum.typeclass

/**
 * Commutative Applicative.
 *
 * Further than an Applicative, which just allows composition of independent effectful functions,
 * in a Commutative Applicative those functions can be composed in any order, which guarantees
 * that their effects do not interfere.
 *
 * Must obey the laws defined in cats.laws.CommutativeApplicativeLaws.
 */
@typeclass trait CommutativeApplicative[F[_]] extends Applicative[F] with CommutativeApply[F]

object CommutativeApplicative {
  def commutativeMonoidFor[F[_]: CommutativeApplicative, A: CommutativeMonoid]: CommutativeMonoid[F[A]] =
    new CommutativeMonoid[F[A]] {
      override def empty: F[A] =
        CommutativeApplicative[F]
          .pure(CommutativeMonoid[A].empty)

      override def combine(x: F[A], y: F[A]): F[A] =
        CommutativeApplicative[F]
          .map2(x, y)(CommutativeMonoid[A].combine)
    }

  /* ======================================================================== */
  /* THE FOLLOWING CODE IS MANAGED BY SIMULACRUM; PLEASE DO NOT EDIT!!!!      */
  /* ======================================================================== */

  /**
   * Summon an instance of [[CommutativeApplicative]] for `F`.
   */
  @inline def apply[F[_]](implicit instance: CommutativeApplicative[F]): CommutativeApplicative[F] = instance

  @deprecated("Use cats.syntax object imports", "2.2.0")
  object ops {
    implicit def toAllCommutativeApplicativeOps[F[_], A](
      target: F[A]
    )(implicit tc: CommutativeApplicative[F]): AllOps[F, A] {
      type TypeClassType = CommutativeApplicative[F]
    } =
      new AllOps[F, A] {
        type TypeClassType = CommutativeApplicative[F]
        val self: F[A] = target
        val typeClassInstance: TypeClassType = tc
      }
  }
  trait Ops[F[_], A] extends Serializable {
    type TypeClassType <: CommutativeApplicative[F]
    def self: F[A]
    val typeClassInstance: TypeClassType
  }
  trait AllOps[F[_], A] extends Ops[F, A] with Applicative.AllOps[F, A] with CommutativeApply.AllOps[F, A] {
    type TypeClassType <: CommutativeApplicative[F]
  }
  trait ToCommutativeApplicativeOps extends Serializable {
    implicit def toCommutativeApplicativeOps[F[_], A](target: F[A])(implicit tc: CommutativeApplicative[F]): Ops[F, A] {
      type TypeClassType = CommutativeApplicative[F]
    } =
      new Ops[F, A] {
        type TypeClassType = CommutativeApplicative[F]
        val self: F[A] = target
        val typeClassInstance: TypeClassType = tc
      }
  }
  @deprecated("Use cats.syntax object imports", "2.2.0")
  object nonInheritedOps extends ToCommutativeApplicativeOps

  /* ======================================================================== */
  /* END OF SIMULACRUM-MANAGED CODE                                           */
  /* ======================================================================== */

}
