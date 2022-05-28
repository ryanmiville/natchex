package natchex.examples

import cats.effect.IO
import org.typelevel.log4cats.Logger

trait PrintlnLogger {
  implicit val logger: Logger[IO] =
    new Logger[IO] {
      def error(message: => String): IO[Unit] =
        IO.println(s"[error] $message\n")
      def warn(message: => String): IO[Unit] =
        IO.println(s"[warn] $message\n")
      def info(message: => String): IO[Unit] =
        IO.println(s"[info] $message\n")
      def debug(message: => String): IO[Unit] =
        IO.println(s"[debug] $message\n")
      def trace(message: => String): IO[Unit] =
        IO.println(s"[trace] $message\n")
      def error(t: Throwable)(message: => String): IO[Unit] =
        IO.println(s"[error] $message\n${t.getMessage}")
      def warn(t: Throwable)(message: => String): IO[Unit] =
        IO.println(s"[warn] $message\n${t.getMessage}")
      def info(t: Throwable)(message: => String): IO[Unit] =
        IO.println(s"[info] $message\n${t.getMessage}")
      def debug(t: Throwable)(message: => String): IO[Unit] =
        IO.println(s"[debug] $message\n${t.getMessage}")
      def trace(t: Throwable)(message: => String): IO[Unit] =
        IO.println(s"[trace] $message\n${t.getMessage}")
    }
}
