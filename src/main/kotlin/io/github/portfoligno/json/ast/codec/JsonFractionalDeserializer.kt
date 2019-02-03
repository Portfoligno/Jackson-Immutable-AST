package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonParser.NumberType.*
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_FLOAT
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.JsonBigDecimal
import io.github.portfoligno.json.ast.JsonDouble
import io.github.portfoligno.json.ast.JsonFloat
import io.github.portfoligno.json.ast.JsonFractional

internal
object JsonFractionalDeserializer : ExpectedTokenDeserializer<JsonFractional>(VALUE_NUMBER_FLOAT) {
  override
  operator fun invoke(p: JsonParser, context: DeserializationContext): JsonFractional =
      when (p.numberType) {
        FLOAT -> JsonFloat(p.floatValue)
        DOUBLE -> JsonDouble(p.doubleValue)
        BIG_DECIMAL -> JsonBigDecimal(p.decimalValue)
        else -> throw reportInputMismatch(context, "Fractional value expected, but ${p.numberValue} is found")
      }
}
