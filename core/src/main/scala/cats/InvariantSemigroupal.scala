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

import simulacrum.typeclass

/**
 * [[InvariantSemigroupal]] is nothing more than something both invariant
 * and Semigroupal. It comes up enough to be useful, and composes well
 */
@typeclass trait InvariantSemigroupal[F[_]] extends Semigroupal[F] with Invariant[F] { self =>

  def composeApply[G[_]: Apply]: InvariantSemigroupal[λ[α => F[G[α]]]] =
    new ComposedInvariantApplySemigroupal[F, G] {
      def F = self
      def G = Apply[G]
    }

}

object InvariantSemigroupal extends SemigroupalArityFunctions {

  /**
   * Gives a `Semigroup` instance if A itself has a `Semigroup` instance.
   */
  def semigroup[F[_], A](implicit F: InvariantSemigroupal[F], A: Semigroup[A]): Semigroup[F[A]] =
    new InvariantSemigroupalSemigroup[F, A](F, A)

  /* ======================================================================== */
  /* THE FOLLOWING CODE IS MANAGED BY SIMULACRUM; PLEASE DO NOT EDIT!!!!      */
  /* ======================================================================== */

  /**
   * Summon an instance of [[InvariantSemigroupal]] for `F`.
   */
  @inline def apply[F[_]](implicit instance: InvariantSemigroupal[F]): InvariantSemigroupal[F] = instance

  @deprecated("Use cats.syntax object imports", "2.2.0")
  object ops {
    implicit def toAllInvariantSemigroupalOps[F[_], A](
      target: F[A]
    )(implicit tc: InvariantSemigroupal[F]): AllOps[F, A] {
      type TypeClassType = InvariantSemigroupal[F]
    } =
      new AllOps[F, A] {
        type TypeClassType = InvariantSemigroupal[F]
        val self: F[A] = target
        val typeClassInstance: TypeClassType = tc
      }
  }
  trait Ops[F[_], A] extends Serializable {
    type TypeClassType <: InvariantSemigroupal[F]
    def self: F[A]
    val typeClassInstance: TypeClassType
  }
  trait AllOps[F[_], A] extends Ops[F, A] with Semigroupal.AllOps[F, A] with Invariant.AllOps[F, A] {
    type TypeClassType <: InvariantSemigroupal[F]
  }
  trait ToInvariantSemigroupalOps extends Serializable {
    implicit def toInvariantSemigroupalOps[F[_], A](target: F[A])(implicit tc: InvariantSemigroupal[F]): Ops[F, A] {
      type TypeClassType = InvariantSemigroupal[F]
    } =
      new Ops[F, A] {
        type TypeClassType = InvariantSemigroupal[F]
        val self: F[A] = target
        val typeClassInstance: TypeClassType = tc
      }
  }
  @deprecated("Use cats.syntax object imports", "2.2.0")
  object nonInheritedOps extends ToInvariantSemigroupalOps

  /* ======================================================================== */
  /* END OF SIMULACRUM-MANAGED CODE                                           */
  /* ======================================================================== */

}

private[cats] class InvariantSemigroupalSemigroup[F[_], A](f: InvariantSemigroupal[F], sg: Semigroup[A])
    extends Semigroup[F[A]] {
  def combine(a: F[A], b: F[A]): F[A] =
    InvariantSemigroupal.imap2(a, b)(sg.combine)(a => (a, a))(f, f)
}
