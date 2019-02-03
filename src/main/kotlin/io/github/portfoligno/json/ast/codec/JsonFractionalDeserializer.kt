package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonParser.NumberType.*
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.JsonBigDecimal
import io.github.portfoligno.json.ast.JsonDouble
import io.github.portfoligno.json.ast.JsonFloat
import io.github.portfoligno.json.ast.JsonFractional

internal
object JsonFractionalDeserializer : Deserializer<JsonFractional>() {
  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): JsonFractional {
    checkCurrentToken(p, ctxt, JsonToken.VALUE_NUMBER_FLOAT)
    return invoke(p, ctxt)
  }

  operator fun invoke(p: JsonParser, context: DeserializationContext): JsonFractional =
      when (p.numberType) {
        FLOAT -> JsonFloat(p.floatValue)
        DOUBLE -> JsonDouble(p.doubleValue)
        BIG_DECIMAL -> JsonBigDecimal(p.decimalValue)
        else -> throw reportInputMismatch(context, "Fractional value expected, but ${p.numberValue} is found")
      }
}
