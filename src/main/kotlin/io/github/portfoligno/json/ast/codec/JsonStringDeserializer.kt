package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.VALUE_STRING
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.JsonString

internal
object JsonStringDeserializer : ExpectedTokenDeserializer<JsonString>(VALUE_STRING) {
  override
  operator fun invoke(p: JsonParser, context: DeserializationContext): JsonString =
      JsonString(p.valueAsString)
}
