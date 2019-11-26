@file:Suppress("BlockingMethodInNonBlockingContext")
package io.github.portfoligno.json.ast.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.POJONode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.github.portfoligno.json.ast.Json
import io.github.portfoligno.json.ast.JsonFalse
import io.github.portfoligno.json.ast.JsonNull
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class JsonCodecSpec : StringSpec({
  val m = ObjectMapper()

  """readValue from "null" should work""" {
    m.readValue<Json>("null") shouldBe JsonNull
  }
  """readValue from "false" should work""" {
    m.readValue<Json>("false") shouldBe JsonFalse
  }
  """convertValue should work""" {
    m.convertValue<Json>(this)
  }
  """treeToValue with `POJONode` should work""" {
    m.treeToValue<Json>(ArrayNode(
        m.nodeFactory,
        listOf(POJONode(1), POJONode(this))))
  }
  """writeValueAsString(JsonNull) should be "null""" {
    m.writeValueAsString(JsonNull) shouldBe "null"
  }
  """writeValueAsString(JsonFalse) should be "false"""" {
    m.writeValueAsString(JsonFalse) shouldBe "false"
  }
  """Round-trip should work""" {
    val s = m.writeValueAsString(this)
    m.writeValueAsString(m.readValue<Json>(s)) shouldBe s
  }
  """plainValue should have the same output""" {
    val json = m.convertValue<Json>(this)
    m.writeValueAsString(json.plainValue) shouldBe m.writeValueAsString(json)
  }
})
