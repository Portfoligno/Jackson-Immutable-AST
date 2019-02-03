@file:Suppress("SortModifiers")
package io.github.portfoligno.json.ast

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.google.common.collect.*
import io.github.portfoligno.json.ast.codec.*
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.collections.Map.Entry

@JsonDeserialize(using = JsonDeserializer::class)
@JsonSerialize(using = JsonSerializer::class)
sealed class Json {
  abstract val value: Any?

  open val plainValue: Any?
    get() = value

  internal
  abstract fun toTokens(generator: JsonGenerator)

  override
  fun toString(): String =
      "${javaClass.simpleName}($value)"

  companion object {
    @JvmStatic
    fun from(value: Any?): Json =
        when (value) {
          null -> JsonNull
          false -> JsonFalse
          true -> JsonTrue
          is String -> JsonString(value)
          is BigDecimal -> JsonBigDecimal(value)
          is Double -> JsonDouble(value)
          is Float -> JsonFloat(value)
          is BigInteger -> JsonBigInteger(value)
          is Long -> JsonLong(value)
          is Int -> JsonInteger(value)
          is Iterable<*> -> JsonArray.from(value)
          is Map<*, *> -> JsonObject.from(value.entries)
          else -> throw IllegalArgumentException(value.toString())
        }
  }
}


@JsonDeserialize(using = JsonNullDeserializer::class)
object JsonNull : Json() {
  override
  val value: Nothing?
    get() = null

  override
  val plainValue: Nothing?
    get() = value

  override
  fun toTokens(generator: JsonGenerator): Unit =
      generator.writeNull()

  override
  fun toString(): String =
      javaClass.simpleName
}

@JsonDeserialize(using = JsonNonNullDeserializer::class)
sealed class JsonNonNull : Json() {
  override
  abstract val value: Any

  override
  val plainValue: Any
    get() = value
}


@JsonDeserialize(using = JsonPrimitiveDeserializer::class)
sealed class JsonPrimitive : JsonNonNull()

@JsonDeserialize(using = JsonCollectionDeserializer::class)
sealed class JsonCollection : JsonNonNull()


@JsonDeserialize(using = JsonBooleanDeserializer::class)
sealed class JsonBoolean(override val value: Boolean) : JsonPrimitive() {
  override
  val plainValue: Boolean
    get() = value

  override
  fun toTokens(generator: JsonGenerator): Unit =
      generator.writeBoolean(value)

  override
  fun toString(): String =
      javaClass.simpleName
}

@JsonDeserialize(using = JsonStringDeserializer::class)
data class JsonString(override val value: String) : JsonPrimitive() {
  override
  val plainValue: String
    get() = value

  override
  fun toTokens(generator: JsonGenerator): Unit =
      generator.writeString(value)

  override
  fun toString(): String =
      super.toString()
}

@JsonDeserialize(using = JsonNumberDeserializer::class)
sealed class JsonNumber : JsonPrimitive() {
  override
  abstract val value: Number

  override
  val plainValue: Number
    get() = value
}


@JsonDeserialize(using = JsonFalseDeserializer::class)
object JsonFalse : JsonBoolean(false)

@JsonDeserialize(using = JsonTrueDeserializer::class)
object JsonTrue : JsonBoolean(true)


@JsonDeserialize(using = JsonFractionalDeserializer::class)
sealed class JsonFractional : JsonNumber()

@JsonDeserialize(using = JsonIntegralDeserializer::class)
sealed class JsonIntegral : JsonNumber()


@JsonDeserialize(using = JsonBigDecimalDeserializer::class)
data class JsonBigDecimal(override val value: BigDecimal) : JsonFractional() {
  override
  val plainValue: BigDecimal
    get() = value

  override
  fun toTokens(generator: JsonGenerator): Unit =
      generator.writeNumber(value)

  override
  fun hashCode(): Int =
      value.toDouble().hashCode()

  override
  fun equals(other: Any?): Boolean =
      when (other) {
        is JsonBigDecimal -> value.compareTo(other.value) == 0
        is JsonDouble -> value.compareTo(other.value.toBigDecimal()) == 0
        is JsonFloat -> value.compareTo(other.value.toBigDecimal()) == 0
        is JsonBigInteger -> value.compareTo(other.value.toBigDecimal()) == 0
        is JsonLong -> value.compareTo(other.value.toBigDecimal()) == 0
        is JsonInteger -> value.compareTo(other.value.toBigDecimal()) == 0
        else -> false
      }

  override
  fun toString(): String =
      super.toString()
}

@JsonDeserialize(using = JsonDoubleDeserializer::class)
data class JsonDouble(override val value: Double) : JsonFractional() {
  init {
    require(value.isFinite())
  }

  override
  val plainValue: Double
    get() = value

  override
  fun toTokens(generator: JsonGenerator): Unit =
      generator.writeNumber(value)

  override
  fun hashCode(): Int =
      value.hashCode()

  override
  fun equals(other: Any?): Boolean =
      when (other) {
        is JsonBigDecimal -> value.toBigDecimal().compareTo(other.value) == 0
        is JsonDouble -> value == other.value
        is JsonFloat -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonBigInteger -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonLong -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonInteger -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        else -> false
      }

  override
  fun toString(): String =
      super.toString()
}

@JsonDeserialize(using = JsonFloatDeserializer::class)
data class JsonFloat(override val value: Float) : JsonFractional() {
  init {
    require(value.isFinite())
  }

  override
  val plainValue: Float
    get() = value

  override
  fun toTokens(generator: JsonGenerator): Unit =
      generator.writeNumber(value)

  override
  fun hashCode(): Int =
      value.toDouble().hashCode()

  override
  fun equals(other: Any?): Boolean =
      when (other) {
        is JsonBigDecimal -> value.toBigDecimal().compareTo(other.value) == 0
        is JsonDouble -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonFloat -> value == other.value
        is JsonBigInteger -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonLong -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonInteger -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        else -> false
      }

  override
  fun toString(): String =
      super.toString()
}


@JsonDeserialize(using = JsonBigIntegerDeserializer::class)
data class JsonBigInteger(override val value: BigInteger) : JsonIntegral() {
  override
  val plainValue: BigInteger
    get() = value

  override
  fun toTokens(generator: JsonGenerator): Unit =
      generator.writeNumber(value)

  override
  fun hashCode(): Int =
      value.toDouble().hashCode()

  override
  fun equals(other: Any?): Boolean =
      when (other) {
        is JsonBigDecimal -> value.toBigDecimal().compareTo(other.value) == 0
        is JsonDouble -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonFloat -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonBigInteger -> value == other.value
        is JsonLong -> value == other.value.toBigInteger()
        is JsonInteger -> value == other.value.toBigInteger()
        else -> false
      }

  override
  fun toString(): String =
      super.toString()
}

@JsonDeserialize(using = JsonLongDeserializer::class)
data class JsonLong(override val value: Long) : JsonIntegral() {
  override
  val plainValue: Long
    get() = value

  override
  fun toTokens(generator: JsonGenerator): Unit =
      generator.writeNumber(value)

  override
  fun hashCode(): Int =
      value.toDouble().hashCode()

  override
  fun equals(other: Any?): Boolean =
      when (other) {
        is JsonBigDecimal -> value.toBigDecimal().compareTo(other.value) == 0
        is JsonDouble -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonFloat -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonBigInteger -> value.toBigInteger() == other.value
        is JsonLong -> value == other.value
        is JsonInteger -> value == other.value.toLong()
        else -> false
      }

  override
  fun toString(): String =
      super.toString()
}

@JsonDeserialize(using = JsonIntegerDeserializer::class)
data class JsonInteger(override val value: Int) : JsonIntegral() {
  override
  val plainValue: Int
    get() = value

  override
  fun toTokens(generator: JsonGenerator): Unit =
      generator.writeNumber(value)

  override
  fun hashCode(): Int =
      value.toDouble().hashCode()

  override
  fun equals(other: Any?): Boolean =
      when (other) {
        is JsonBigDecimal -> value.toBigDecimal().compareTo(other.value) == 0
        is JsonDouble -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonFloat -> value.toBigDecimal().compareTo(other.value.toBigDecimal()) == 0
        is JsonBigInteger -> value.toBigInteger() == other.value
        is JsonLong -> value.toLong() == other.value
        is JsonInteger -> value == other.value
        else -> false
      }

  override
  fun toString(): String =
      super.toString()
}


@JsonDeserialize(using = JsonArrayDeserializer::class)
data class JsonArray(private val elements: ImmutableList<Json>) : JsonCollection() {
  override
  val value: List<Json>
    get() = elements

  override
  val plainValue: List<Any?>
    get() = Lists.transform(elements) { it!!.plainValue }

  override
  fun toTokens(generator: JsonGenerator) {
    generator.writeStartArray(elements.size)
    elements.forEach {
      it.toTokens(generator)
    }
    generator.writeEndArray()
  }

  override
  fun toString(): String =
      super.toString()

  companion object {
    @JvmStatic
    fun from(value: Iterable<Any?>): JsonArray =
        JsonArray(ImmutableList.copyOf(Iterables.transform(value, Json.Companion::from)))
  }
}

@JsonDeserialize(using = JsonObjectDeserializer::class)
data class JsonObject(private val elements: ImmutableMap<String, Json>) : JsonCollection() {
  override
  val value: Map<String, Json>
    get() = elements

  override
  val plainValue: Map<String, Any?>
    get() = Maps.transformValues(elements) { it!!.plainValue }

  override
  fun toTokens(generator: JsonGenerator) {
    generator.writeStartObject(this)
    elements.forEach { (k, v) ->
      generator.writeFieldName(k)
      v.toTokens(generator)
    }
    generator.writeEndObject()
  }

  override
  fun toString(): String =
      super.toString()

  companion object {
    @JvmStatic
    fun from(value: Map<String, Any?>): JsonObject =
        from(value.entries)

    @Suppress("UnstableApiUsage")
    internal
    fun from(entries: Iterable<Entry<*, *>>): JsonObject =
        JsonObject(ImmutableMap.copyOf(Iterables.transform(entries) {
          val (k, v) = it!!
          Maps.immutableEntry(k as? String ?: throw IllegalArgumentException(k.toString()), from(v))
        }))
  }
}
