package natchex.examples

import cats.effect.{IO, IOApp}
import fs2.Stream
import natchex._
import natchez.log.Log

import scala.concurrent.duration.DurationInt

object StreamExample extends IOApp.Simple with PrintlnLogger {
  val entryPoint = Log.entryPoint[IO]("stream-example")

  def sleep(implicit trace: Trace[IO]) =
    trace.spanStream("sleep_span")(Stream.sleep[IO](1.second))

  def run: IO[Unit] = {
    Trace.make(entryPoint).flatMap { implicit trace =>
      val twice = sleep ++ sleep
      trace.root("stream_root")(twice.compile.drain)
    }
  }
}

/**
  [info] {
  "name" : "stream_root",
  "service" : "stream-example",
  "timestamp" : "2022-05-28T17:25:45.899435Z",
  "duration_ms" : 2315,
  "trace.span_id" : "711057ec-2e26-4140-80bd-8891c8c788a9",
  "trace.parent_id" : null,
  "trace.trace_id" : "13c8edf0-55aa-4c2f-979f-d6f9fe6c61ff",
  "exit.case" : "succeeded",
  "children" : [
    {
      "name" : "sleep_span",
      "service" : "stream-example",
      "timestamp" : "2022-05-28T17:25:46.116070Z",
      "duration_ms" : 1048,
      "trace.span_id" : "8f8acd60-e0d3-40db-8f6f-b9d220513465",
      "trace.parent_id" : "13c8edf0-55aa-4c2f-979f-d6f9fe6c61ff",
      "trace.trace_id" : "13c8edf0-55aa-4c2f-979f-d6f9fe6c61ff",
      "exit.case" : "succeeded",
      "children" : [
      ]
    },
    {
      "name" : "sleep_span",
      "service" : "stream-example",
      "timestamp" : "2022-05-28T17:25:47.209428Z",
      "duration_ms" : 1004,
      "trace.span_id" : "6f90d6ac-6397-4809-b9e8-4fd3b01df761",
      "trace.parent_id" : "13c8edf0-55aa-4c2f-979f-d6f9fe6c61ff",
      "trace.trace_id" : "13c8edf0-55aa-4c2f-979f-d6f9fe6c61ff",
      "exit.case" : "succeeded",
      "children" : [
      ]
    }
  ]
}
  */
