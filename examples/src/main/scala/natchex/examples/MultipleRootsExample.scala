package natchex.examples

import cats.syntax.all._
import cats.effect.{IO, IOApp}
import fs2.Stream
import natchex.Trace
import natchez.log.Log

import scala.concurrent.duration.DurationInt

object MultipleRootsExample extends IOApp.Simple with PrintlnLogger {
  val entryPoint = Log.entryPoint[IO]("multiple-roots-example")

  val stream = Stream.range(1, 11)

  def run: IO[Unit] =
    Trace.make(entryPoint).flatMap { implicit trace =>
      stream
        .chunkN(3)
        .evalMap { chunk =>
          trace.root("batch_root") {
            trace.span("child") {
              trace.put("vals" -> chunk.toList.mkString(",")) >>
                IO.sleep(1.second)
            }
          }
        }
        .compile
        .drain
    }
}

/**
[info] {
  "name" : "batch_root",
  "service" : "multiple-roots-example",
  "timestamp" : "2022-05-28T18:03:27.565071Z",
  "duration_ms" : 1100,
  "trace.span_id" : "3886ed3d-450b-4bde-b486-140def30b8ed",
  "trace.parent_id" : null,
  "trace.trace_id" : "2790b0b0-d83a-46ae-93b8-414690852a73",
  "exit.case" : "succeeded",
  "children" : [
    {
      "name" : "child",
      "service" : "multiple-roots-example",
      "timestamp" : "2022-05-28T18:03:27.577606Z",
      "duration_ms" : 1036,
      "trace.span_id" : "4d0c8d96-173a-47c0-8012-4700b8c42918",
      "trace.parent_id" : "2790b0b0-d83a-46ae-93b8-414690852a73",
      "trace.trace_id" : "2790b0b0-d83a-46ae-93b8-414690852a73",
      "exit.case" : "succeeded",
      "vals" : "1,2,3",
      "children" : [
      ]
    }
  ]
}

[info] {
  "name" : "batch_root",
  "service" : "multiple-roots-example",
  "timestamp" : "2022-05-28T18:03:28.680967Z",
  "duration_ms" : 1003,
  "trace.span_id" : "535e23da-1fde-41ac-8df5-fe6951bc16e0",
  "trace.parent_id" : null,
  "trace.trace_id" : "ec0185b5-a4ae-4f00-8c87-33647b016707",
  "exit.case" : "succeeded",
  "children" : [
    {
      "name" : "child",
      "service" : "multiple-roots-example",
      "timestamp" : "2022-05-28T18:03:28.681218Z",
      "duration_ms" : 1002,
      "trace.span_id" : "2ad321a0-cdd2-4dc0-8151-492055014e1e",
      "trace.parent_id" : "ec0185b5-a4ae-4f00-8c87-33647b016707",
      "trace.trace_id" : "ec0185b5-a4ae-4f00-8c87-33647b016707",
      "exit.case" : "succeeded",
      "vals" : "4,5,6",
      "children" : [
      ]
    }
  ]
}

[info] {
  "name" : "batch_root",
  "service" : "multiple-roots-example",
  "timestamp" : "2022-05-28T18:03:29.685677Z",
  "duration_ms" : 1003,
  "trace.span_id" : "5be708cf-47ac-4920-87b7-8defda4be8ce",
  "trace.parent_id" : null,
  "trace.trace_id" : "5ac2b532-17a6-4ad7-b06a-9fa78d15133b",
  "exit.case" : "succeeded",
  "children" : [
    {
      "name" : "child",
      "service" : "multiple-roots-example",
      "timestamp" : "2022-05-28T18:03:29.685983Z",
      "duration_ms" : 1003,
      "trace.span_id" : "8a7ff9ad-4f80-49ef-a3e0-cdb55ceab654",
      "trace.parent_id" : "5ac2b532-17a6-4ad7-b06a-9fa78d15133b",
      "trace.trace_id" : "5ac2b532-17a6-4ad7-b06a-9fa78d15133b",
      "exit.case" : "succeeded",
      "vals" : "7,8,9",
      "children" : [
      ]
    }
  ]
}

[info] {
  "name" : "batch_root",
  "service" : "multiple-roots-example",
  "timestamp" : "2022-05-28T18:03:30.691311Z",
  "duration_ms" : 1004,
  "trace.span_id" : "479c4706-1b6c-4f4d-adbe-70b841ee3148",
  "trace.parent_id" : null,
  "trace.trace_id" : "4d538c83-e444-48a9-a642-835b07efc0a5",
  "exit.case" : "succeeded",
  "children" : [
    {
      "name" : "child",
      "service" : "multiple-roots-example",
      "timestamp" : "2022-05-28T18:03:30.691547Z",
      "duration_ms" : 1004,
      "trace.span_id" : "ad51a74b-763f-4b0e-a326-cabf813866bb",
      "trace.parent_id" : "4d538c83-e444-48a9-a642-835b07efc0a5",
      "trace.trace_id" : "4d538c83-e444-48a9-a642-835b07efc0a5",
      "exit.case" : "succeeded",
      "vals" : "10",
      "children" : [
      ]
    }
  ]
}
  */
