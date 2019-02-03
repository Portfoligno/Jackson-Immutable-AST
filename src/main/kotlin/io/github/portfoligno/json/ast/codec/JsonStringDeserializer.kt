package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.JsonString

internal
object JsonStringDeserializer : Deserializer<JsonString>() {
  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): JsonString {
    checkCurrentToken(p, ctxt, JsonToken.VALUE_STRING)
    return invoke(p)
  }

  operator fun invoke(p: JsonParser): JsonString =
      JsonString(p.valueAsString)
}
