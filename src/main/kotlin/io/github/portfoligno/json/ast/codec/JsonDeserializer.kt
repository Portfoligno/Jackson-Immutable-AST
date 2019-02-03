package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.*
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.util.TokenBuffer
import io.github.portfoligno.json.ast.Json
import io.github.portfoligno.json.ast.JsonFalse
import io.github.portfoligno.json.ast.JsonNull
import io.github.portfoligno.json.ast.JsonTrue

internal
object JsonDeserializer : Deserializer<Json>() {
  override
  fun getNullValue(ctxt: DeserializationContext): Json =
      JsonNull

  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): Json =
      invoke(p, ctxt)

  operator fun invoke(p: JsonParser, context: DeserializationContext): Json =
      when (p.currentToken()) {
        START_OBJECT -> JsonObjectDeserializer(p, context)
        START_ARRAY -> JsonArrayDeserializer(p, context)
        VALUE_EMBEDDED_OBJECT -> fromEmbedded(p, context)
        VALUE_STRING -> JsonStringDeserializer(p)
        VALUE_NUMBER_INT -> JsonIntegralDeserializer(p, context)
        VALUE_NUMBER_FLOAT -> JsonFractionalDeserializer(p, context)
        VALUE_TRUE -> JsonTrue
        VALUE_FALSE -> JsonFalse
        VALUE_NULL -> JsonNull
        else -> throw reportWrongTokenException(context, VALUE_NULL)
      }

  fun fromEmbedded(p: JsonParser, context: DeserializationContext): Json =
      p.codec.readValue(p, TokenBuffer::class.java).asParser().run {
        nextToken()
        invoke(this, context)
      }
}
