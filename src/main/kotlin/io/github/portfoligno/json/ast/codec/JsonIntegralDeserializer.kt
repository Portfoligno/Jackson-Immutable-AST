package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonParser.NumberType.*
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.JsonBigInteger
import io.github.portfoligno.json.ast.JsonInteger
import io.github.portfoligno.json.ast.JsonIntegral
import io.github.portfoligno.json.ast.JsonLong

internal
object JsonIntegralDeserializer : ExpectedTokenDeserializer<JsonIntegral>(VALUE_NUMBER_INT) {
  override
  operator fun invoke(p: JsonParser, context: DeserializationContext): JsonIntegral =
      when (p.numberType) {
        INT -> JsonInteger(p.intValue)
        LONG -> JsonLong(p.longValue)
        BIG_INTEGER -> JsonBigInteger(p.bigIntegerValue)
        else -> throw reportInputMismatch(context, "Integral value expected, but ${p.numberValue} is found")
      }
}
