@file:Suppress("BlockingMethodInNonBlockingContext")
package io.github.portfoligno.json.ast.test

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import io.github.portfoligno.json.ast.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

data class Wrapper(
    @get:JsonValue
    val json: JsonNonNull // `JsonNull` won't work due to how `JsonValue` is handled
)

data class ObjectWrapper(
    @get:JsonValue
    val json: JsonObject
)

class JsonCodecAnnotationSpec : StringSpec({
  val m = jacksonObjectMapper()

  """`@get:JsonValue` should work with `JsonFalse`""" {
    m.readValue<Wrapper>("false") shouldBe Wrapper(JsonFalse)
  }
  """`@get:JsonValue` should work with `JsonTrue`""" {
    m.readValue<Wrapper>("true") shouldBe Wrapper(JsonTrue)
  }
  """`@get:JsonValue` should work with `JsonInteger`""" {
    m.readValue<Wrapper>("3") shouldBe Wrapper(JsonInteger(3))
  }

  """`@get:JsonValue` should work with `JsonArray`""" {
    val wrapper = Wrapper(JsonArray(ImmutableList.of(m.convertValue<JsonObject>(this))))
    m.readValue<Wrapper>(m.writeValueAsString(wrapper)) shouldBe wrapper
  }
  """`@get:JsonValue` should work with empty `JsonArray`""" {
    val wrapper = Wrapper(JsonArray(ImmutableList.of()))
    m.readValue<Wrapper>(m.writeValueAsString(wrapper)) shouldBe wrapper
  }

  """`@get:JsonValue` should work with `JsonObject`""" {
    val wrapper = Wrapper(m.convertValue<JsonObject>(this))
    m.readValue<Wrapper>(m.writeValueAsString(wrapper)) shouldBe wrapper
  }
  """`@get:JsonValue` should work with empty `JsonObject`""" {
    val wrapper = Wrapper(JsonObject(ImmutableMap.of()))
    m.readValue<Wrapper>(m.writeValueAsString(wrapper)) shouldBe wrapper
  }
  """`@get:JsonValue` should work with invariant `JsonObject`""" {
    val wrapper = ObjectWrapper(m.convertValue(this))
    m.readValue<ObjectWrapper>(m.writeValueAsString(wrapper)) shouldBe wrapper
  }
  """`@get:JsonValue` should work with invariant empty `JsonObject`""" {
    val wrapper = ObjectWrapper(JsonObject(ImmutableMap.of()))
    m.readValue<ObjectWrapper>(m.writeValueAsString(wrapper)) shouldBe wrapper
  }
})
