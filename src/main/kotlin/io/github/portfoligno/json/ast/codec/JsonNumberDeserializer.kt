package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonParser.NumberType.*
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_FLOAT
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.*
import java.math.BigDecimal

internal
class JsonNumberDeserializer : BaseDeserializer<JsonNumber>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonNumber =
      when (p.currentToken()) {
        VALUE_NUMBER_INT -> JsonIntegralDeserializer(p, context)
        VALUE_NUMBER_FLOAT -> JsonFractionalDeserializer(p, context)
        else -> throw reportWrongTokenException(context, VALUE_NUMBER_FLOAT)
      }
}


internal
object JsonFractionalDeserializer : BaseDeserializer<JsonFractional>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonFractional =
      JsonBigDecimal(p.decimalValue)
}

internal
object JsonIntegralDeserializer : BaseDeserializer<JsonIntegral>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonIntegral =
      when (p.numberType) {
        INT -> JsonInteger(p.intValue)
        LONG -> JsonLong(p.longValue)
        BIG_INTEGER -> JsonBigInteger(p.bigIntegerValue)
        else -> try {
          JsonBigInteger(p.decimalValue.toBigIntegerExact())
        } catch (_: ArithmeticException) {
          throw reportWrongTokenException(context, VALUE_NUMBER_INT)
        }
      }
}


internal
class JsonBigDecimalDeserializer : BaseDeserializer<JsonBigDecimal>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonBigDecimal =
      JsonBigDecimal(p.decimalValue)
}

internal
class JsonFloatDeserializer : BaseDeserializer<JsonFloat>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonFloat =
      p.floatValue.let { parsed ->
        if (BigDecimal(parsed.toDouble()).compareTo(p.decimalValue) == 0) {
          JsonFloat(parsed)
        } else {
          val message = "FLOAT expected, but DOUBLE or BIG_DECIMAL (${p.decimalValue}) was found"
          throw reportInputMismatch(context, message)
        }
      }
}

internal
class JsonDoubleDeserializer : BaseDeserializer<JsonDouble>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonDouble =
      p.doubleValue.let { parsed ->
        if (BigDecimal(parsed).compareTo(p.decimalValue) == 0) {
          JsonDouble(parsed)
        } else {
          val message = "DOUBLE expected, but BIG_DECIMAL (${p.decimalValue}) was found"
          throw reportInputMismatch(context, message)
        }
      }
}


internal
class JsonBigIntegerDeserializer : BaseDeserializer<JsonBigInteger>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonBigInteger =
      when (p.currentToken()) {
        VALUE_NUMBER_INT -> JsonBigInteger(p.bigIntegerValue)
        VALUE_NUMBER_FLOAT -> try {
          JsonBigInteger(p.decimalValue.toBigIntegerExact())
        } catch (_: ArithmeticException) {
          throw reportWrongTokenException(context, VALUE_NUMBER_INT)
        }
        else -> throw reportWrongTokenException(context, VALUE_NUMBER_INT)
      }
}

internal
class JsonLongDeserializer : BaseDeserializer<JsonLong>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonLong =
      when (p.currentToken()) {
        VALUE_NUMBER_INT -> JsonLong(p.longValue)
        VALUE_NUMBER_FLOAT -> try {
          JsonLong(p.decimalValue.longValueExact())
        } catch (_: ArithmeticException) {
          throw reportWrongTokenException(context, VALUE_NUMBER_INT)
        }
        else -> throw reportWrongTokenException(context, VALUE_NUMBER_INT)
      }
}

internal
class JsonIntegerDeserializer : BaseDeserializer<JsonInteger>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonInteger =
      when (p.currentToken()) {
        VALUE_NUMBER_INT -> JsonInteger(p.intValue)
        VALUE_NUMBER_FLOAT -> try {
          JsonInteger(p.decimalValue.intValueExact())
        } catch (_: ArithmeticException) {
          throw reportWrongTokenException(context, VALUE_NUMBER_INT)
        }
        else -> throw reportWrongTokenException(context, VALUE_NUMBER_INT)
      }
}
