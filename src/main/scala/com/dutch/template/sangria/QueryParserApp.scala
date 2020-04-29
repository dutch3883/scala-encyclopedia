package com.dutch.template.sangria

object QueryParserApp extends App{
  import sangria.renderer.QueryRenderer
  import sangria.macros._
  import sangria.ast

  val parsed: ast.Value =
    graphqlInput"""
    {
      id: "1234345"
      version: 2 # changed 2 times
      deliveries: [
        {id: 123, received: false, note: null, state: OPEN}
      ]
    }
  """

  println(parsed.renderPretty)

}
