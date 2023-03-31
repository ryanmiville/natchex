# Natchex 

**Most of these ideas are now in Natchez so this repo has been archived**

An EXtension of [NATCHez](https://github.com/tpolecat/natchez)'s `Trace` that supports creating new roots and tracing of `Resource` and `Stream`.

The majority of this code is shamelessly copied from [Bayou](https://github.com/armanbilge/bayou) and [this Natchez pull request](https://github.com/tpolecat/natchez/pull/526).

Natchex adds a handful of methods to `natchez.Trace` that should be intuitive for anyone familiar with [Natchez's API](https://tpolecat.github.io/natchez/reference/index.html).
```scala
trait Trace[F[_]] extends natchez.Trace[F] {
  def root[A](name: String)(k: F[A]): F[A]
  def continue[A](name: String, kernel: Kernel)(k: F[A]): F[A]
  def continueOrElseRoot[A](name: String, kernel: Kernel)(k: F[A]): F[A]

  def spanResource[A](name: String)(k: Resource[F, A]): Resource[F, A]
  def spanStream[A](name: String)(k: Stream[F, A]): Stream[F, A]
}
```

## Creating new roots
The motivating use case for `Trace` having the capability to create new roots is tracing an operation within an infinite `Stream`, such as processing a batch of messages from an Apache Kafka topic.

When you create a new root, only spans within that new root's scope are attached to it. Once the scope is exited, the previous span takes over again.

## Caveats
Natchex's `Trace` is only implemented for `IO`. [The Natchez pull request](https://github.com/tpolecat/natchez/pull/526) that inspired this project proves that it may be possible to support other `F[_]`s, but I have no interest in case it constrains possible future features.

Just like `natchez.Trace`, a `Trace` must always have a _current span_. `Trace.make` instantiates the `Trace` with a no-op span. Make sure you create a new root before spawning child spans.
