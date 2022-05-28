package natchex.examples

import cats.effect.{IO, IOApp, Resource}
import cats.syntax.all._
import natchex.Trace
import natchez.log.Log

import scala.concurrent.duration.DurationInt

object ResourceExample extends IOApp.Simple with PrintlnLogger {
  val entryPoint = Log.entryPoint[IO]("resource-example")

  def sleep(implicit trace: Trace[IO]) =
    trace.spanResource("sleep_span") {
      Resource.sleep[IO](1.second)
    }

  def run: IO[Unit] = {
    Trace.make(entryPoint).flatMap { implicit trace =>
      val twice = sleep >> sleep
      trace.root("resource_root")(twice.use_)
    }
  }
}

/**
  [info] {
  "name" : "resource_root",
  "service" : "resource-example",
  "timestamp" : "2022-05-28T17:47:06.038332Z",
  "duration_ms" : 2144,
  "trace.span_id" : "802401ae-1720-4f5e-9162-f8ade814b85c",
  "trace.parent_id" : null,
  "trace.trace_id" : "a58d5083-0e3c-40c0-868d-de7afc5a7f4a",
  "exit.case" : "succeeded",
  "children" : [
    {
      "name" : "sleep_span",
      "service" : "resource-example",
      "timestamp" : "2022-05-28T17:47:07.066785Z",
      "duration_ms" : 1002,
      "trace.span_id" : "f310f5c8-611a-43f3-973a-211c15f33e03",
      "trace.parent_id" : "a58d5083-0e3c-40c0-868d-de7afc5a7f4a",
      "trace.trace_id" : "a58d5083-0e3c-40c0-868d-de7afc5a7f4a",
      "exit.case" : "succeeded",
      "children" : [
      ]
    },
    {
      "name" : "sleep_span",
      "service" : "resource-example",
      "timestamp" : "2022-05-28T17:47:06.050985Z",
      "duration_ms" : 2132,
      "trace.span_id" : "0192eea5-f4b9-4470-a86d-d324e79d57df",
      "trace.parent_id" : "a58d5083-0e3c-40c0-868d-de7afc5a7f4a",
      "trace.trace_id" : "a58d5083-0e3c-40c0-868d-de7afc5a7f4a",
      "exit.case" : "succeeded",
      "children" : [
      ]
    }
  ]
}
  */
