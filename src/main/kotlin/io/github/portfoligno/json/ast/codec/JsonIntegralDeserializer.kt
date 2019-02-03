package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonParser.NumberType.*
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.JsonBigInteger
import io.github.portfoligno.json.ast.JsonInteger
import io.github.portfoligno.json.ast.JsonIntegral
import io.github.portfoligno.json.ast.JsonLong

internal
object JsonIntegralDeserializer : Deserializer<JsonIntegral>() {
  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): JsonIntegral {
    checkCurrentToken(p, ctxt, JsonToken.VALUE_NUMBER_INT)
    return invoke(p, ctxt)
  }

  operator fun invoke(p: JsonParser, context: DeserializationContext): JsonIntegral =
      when (p.numberType) {
        INT -> JsonInteger(p.intValue)
        LONG -> JsonLong(p.longValue)
        BIG_INTEGER -> JsonBigInteger(p.bigIntegerValue)
        else -> throw reportInputMismatch(context, "Integral value expected, but ${p.numberValue} is found")
      }
}
