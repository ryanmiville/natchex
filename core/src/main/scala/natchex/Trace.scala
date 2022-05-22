package natchex

import cats.effect.{IO, IOLocal, Resource}
import cats.~>
import natchez.EntryPoint
import natchez.Kernel
import natchez.Span
import natchez.TraceValue
import fs2.Stream

import java.net.URI

trait Trace[F[_]] extends natchez.Trace[F] {
  def root[A](name: String)(k: F[A]): F[A]
  def continue[A](name: String, kernel: Kernel)(k: F[A]): F[A]
  def continueOrElseRoot[A](name: String, kernel: Kernel)(k: F[A]): F[A]

  def spanResource[A](name: String)(k: Resource[F, A]): Resource[F, A]
  def spanStream[A](name: String)(k: Stream[F, A]): Stream[F, A]
}

object Trace {

  /**
    * make a new instance of Trace suspended in IO. It is initialized with a no-op span
    * which can be replaced by calling one of the continue/root methods, which
    * will use `entrypoint`
    */
  def make(entryPoint: EntryPoint[IO]): IO[Trace[IO]] =
    IOLocal(noopSpan).map(new TraceImpl(_, entryPoint))

  final private class TraceImpl(local: IOLocal[Span[IO]], ep: EntryPoint[IO])
      extends Trace[IO] {

    def put(fields: (String, TraceValue)*): IO[Unit] =
      local.get.flatMap(_.put(fields: _*))

    def kernel: IO[Kernel] =
      local.get.flatMap(_.kernel)

    def spanK(name: String): Resource[IO, IO ~> IO] =
      for {
        parent <- Resource.eval(local.get)
        child <- parent.span(name)
      } yield
        new (IO ~> IO) {
          def apply[A](fa: IO[A]): IO[A] =
            local.set(child).bracket(_ => fa)(_ => local.set(parent))
        }

    def span[A](name: String)(k: IO[A]): IO[A] =
      spanK(name).use(f => f(k))

    def traceId: IO[Option[String]] =
      local.get.flatMap(_.traceId)

    def traceUri: IO[Option[URI]] =
      local.get.flatMap(_.traceUri)

    def root[A](name: String)(k: IO[A]): IO[A] =
      transformSpan(ep.root(name)).use(f => f(k))

    def continue[A](name: String, kernel: Kernel)(k: IO[A]): IO[A] =
      transformSpan(ep.continue(name, kernel)).use(f => f(k))

    def continueOrElseRoot[A](name: String, kernel: Kernel)(k: IO[A]): IO[A] =
      transformSpan(ep.continueOrElseRoot(name, kernel)).use(f => f(k))

    def transformSpan(span: Resource[IO, Span[IO]]): Resource[IO, IO ~> IO] =
      for {
        s <- span
        orig <- Resource.eval(local.get)
      } yield
        new (IO ~> IO) {
          def apply[A](fa: IO[A]): IO[A] =
            local.set(s).bracket(_ => fa)(_ => local.set(orig))
        }

    def spanResource[A](name: String)(k: Resource[IO, A]): Resource[IO, A] =
      spanK(name).flatMap { f =>
        Resource(f(k.allocated).map {
          case (a, release) =>
            a -> f(release)
        })
      }

    def spanStream[A](name: String)(k: Stream[IO, A]): Stream[IO, A] =
      Stream.resource(spanK(name)).flatMap(k.translate)
  }

  private val noopSpan: Span[IO] = NoopSpan()

  private case class NoopSpan() extends Span[IO] {
    def put(fields: (String, TraceValue)*): IO[Unit] = IO.unit
    def kernel: IO[Kernel] = IO(Kernel(Map.empty))
    def span(name: String): Resource[IO, Span[IO]] = Resource.pure(noopSpan)
    def traceId: IO[Option[String]] = IO(None)
    def spanId: IO[Option[String]] = IO(None)
    def traceUri: IO[Option[URI]] = IO(None)
  }
}
