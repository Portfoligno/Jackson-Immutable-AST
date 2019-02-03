package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonParser.NumberType.*
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_FLOAT
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.*

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
object JsonFractionalDeserializer : ExpectedTokenDeserializer<JsonFractional>(VALUE_NUMBER_FLOAT) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonFractional =
      when (p.numberType) {
        FLOAT -> JsonFloat(p.floatValue)
        DOUBLE -> JsonDouble(p.doubleValue)
        BIG_DECIMAL -> JsonBigDecimal(p.decimalValue)
        else -> throw reportInputMismatch(context, "Fractional value expected, but ${p.numberType} is found")
      }
}

internal
object JsonIntegralDeserializer : ExpectedTokenDeserializer<JsonIntegral>(VALUE_NUMBER_INT) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonIntegral =
      when (p.numberType) {
        INT -> JsonInteger(p.intValue)
        LONG -> JsonLong(p.longValue)
        BIG_INTEGER -> JsonBigInteger(p.bigIntegerValue)
        else -> throw reportInputMismatch(context, "Integral value expected, but ${p.numberType} is found")
      }
}


internal
object JsonBigDecimalDeserializer : ExpectedTokenDeserializer<JsonBigDecimal>(VALUE_NUMBER_FLOAT) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonBigDecimal =
      when (p.numberType) {
        BIG_DECIMAL -> JsonBigDecimal(p.decimalValue)
        else -> throw reportInputMismatch(context, "BigDecimal expected, but ${p.numberType} is found")
      }
}

internal
object JsonFloatDeserializer : ExpectedTokenDeserializer<JsonFloat>(VALUE_NUMBER_FLOAT) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonFloat =
      when (p.numberType) {
        FLOAT -> JsonFloat(p.floatValue)
        else -> throw reportInputMismatch(context, "Float expected, but ${p.numberType} is found")
      }
}

internal
object JsonDoubleDeserializer : ExpectedTokenDeserializer<JsonDouble>(VALUE_NUMBER_FLOAT) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonDouble =
      when (p.numberType) {
        DOUBLE -> JsonDouble(p.doubleValue)
        else -> throw reportInputMismatch(context, "Double expected, but ${p.numberType} is found")
      }
}


internal
object JsonBigIntegerDeserializer : ExpectedTokenDeserializer<JsonBigInteger>(VALUE_NUMBER_INT) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonBigInteger =
      when (p.numberType) {
        BIG_INTEGER -> JsonBigInteger(p.bigIntegerValue)
        else -> throw reportInputMismatch(context, "BigInteger expected, but ${p.numberType} is found")
      }
}

internal
object JsonLongDeserializer : ExpectedTokenDeserializer<JsonLong>(VALUE_NUMBER_INT) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonLong =
      when (p.numberType) {
        LONG -> JsonLong(p.longValue)
        else -> throw reportInputMismatch(context, "Long expected, but ${p.numberType} is found")
      }
}

internal
object JsonIntegerDeserializer : ExpectedTokenDeserializer<JsonInteger>(VALUE_NUMBER_INT) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonInteger =
      when (p.numberType) {
        INT -> JsonInteger(p.intValue)
        else -> throw reportInputMismatch(context, "Integer expected, but ${p.numberType} is found")
      }
}
