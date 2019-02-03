@file:Suppress("SortModifiers")
package io.github.portfoligno.json.ast

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import java.math.BigDecimal
import java.math.BigInteger

sealed class Json {
  abstract val value: Any?

  override
  fun toString(): String =
      "${javaClass.simpleName}($value)"
}


object JsonNull : Json() {
  override
  val value: Nothing?
    get() = null

  override
  fun toString(): String =
      javaClass.simpleName
}

sealed class JsonNonNull : Json() {
  override
  abstract val value: Any
}


sealed class JsonPrimitive : JsonNonNull()

sealed class JsonContainer : JsonNonNull()


sealed class JsonBoolean(override val value: Boolean) : JsonPrimitive() {
  override
  fun toString(): String =
      javaClass.simpleName
}

data class JsonString(override val value: String) : JsonPrimitive() {
  override
  fun toString(): String =
      super.toString()
}

sealed class JsonNumber : JsonPrimitive() {
  override
  abstract val value: Number
}


object JsonFalse : JsonBoolean(false)

object JsonTrue : JsonBoolean(true)


sealed class JsonFractional : JsonNumber()

sealed class JsonIntegral : JsonNumber()


data class JsonBigDecimal(override val value: BigDecimal) : JsonFractional() {
  override
  fun toString(): String =
      super.toString()
}

data class JsonDouble(override val value: Double) : JsonFractional() {
  override
  fun toString(): String =
      super.toString()
}

data class JsonFloat(override val value: Float) : JsonFractional() {
  override
  fun toString(): String =
      super.toString()
}


data class JsonBigInteger(override val value: BigInteger) : JsonIntegral() {
  override
  fun toString(): String =
      super.toString()
}

data class JsonLong(override val value: Long) : JsonIntegral() {
  override
  fun toString(): String =
      super.toString()
}

data class JsonInteger(override val value: Int) : JsonIntegral() {
  override
  fun toString(): String =
      super.toString()
}


data class JsonArray(private val elements: ImmutableList<Json>) : JsonContainer() {
  override
  val value: List<Json>
    get() = elements

  override
  fun toString(): String =
      super.toString()
}

data class JsonObject(private val elements: ImmutableMap<String, Json>) : JsonContainer() {
  override
  val value: Map<String, Json>
    get() = elements

  override
  fun toString(): String =
      super.toString()
}
