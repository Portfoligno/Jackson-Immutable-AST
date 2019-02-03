package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.*
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.JsonBoolean
import io.github.portfoligno.json.ast.JsonFalse
import io.github.portfoligno.json.ast.JsonTrue

internal
class JsonBooleanDeserializer : BaseDeserializer<JsonBoolean>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonBoolean =
      when (p.currentToken()) {
        VALUE_TRUE -> JsonTrue
        VALUE_FALSE -> JsonFalse
        else -> throw reportWrongTokenException(context, VALUE_FALSE)
      }
}


internal
class JsonFalseDeserializer : ExpectedTokenDeserializer<JsonFalse>(VALUE_FALSE) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonFalse =
      JsonFalse
}

internal
class JsonTrueDeserializer : ExpectedTokenDeserializer<JsonTrue>(VALUE_TRUE) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonTrue =
      JsonTrue
}
