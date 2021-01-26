package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.*
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.ast.*

internal
object JsonDeserializer : BaseDeserializer<Json>() {
  override
  fun getNullValue(ctxt: DeserializationContext): Json =
      JsonNull

  override
  fun invoke(p: JsonParser, context: DeserializationContext): Json =
      when (p.currentToken()) {
        START_OBJECT -> JsonObjectDeserializer(p, context)
        START_ARRAY -> JsonArrayDeserializer(p, context)
        VALUE_STRING -> JsonStringDeserializer(p, context)
        VALUE_NUMBER_INT -> JsonIntegralDeserializer(p, context)
        VALUE_NUMBER_FLOAT -> JsonFractionalDeserializer(p, context)
        VALUE_TRUE -> JsonTrue
        VALUE_FALSE -> JsonFalse
        VALUE_NULL -> JsonNull
        // `JsonValue` has special handling that skips `START_OBJECT` prematurely
        END_OBJECT, FIELD_NAME -> JsonObjectDeserializer(p, context, useCurrentToken = true)
        else -> throw reportWrongTokenException(context, VALUE_NULL)
      }

  fun fromEmbedded(p: JsonParser, context: DeserializationContext): Json =
      p.currentValueTokens(context).let {
        it.nextToken()
        invoke(it, context)
      }
}


internal
class JsonNullDeserializer : ExpectedTokenDeserializer<JsonNull>(VALUE_NULL) {
  override
  fun getNullValue(ctxt: DeserializationContext?): JsonNull =
      JsonNull

  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonNull =
      JsonNull
}

internal
class JsonNonNullDeserializer : BaseDeserializer<JsonNonNull>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonNonNull =
      when (p.currentToken()) {
        START_OBJECT -> JsonObjectDeserializer(p, context)
        START_ARRAY -> JsonArrayDeserializer(p, context)
        VALUE_STRING -> JsonStringDeserializer(p, context)
        VALUE_NUMBER_INT -> JsonIntegralDeserializer(p, context)
        VALUE_NUMBER_FLOAT -> JsonFractionalDeserializer(p, context)
        VALUE_TRUE -> JsonTrue
        VALUE_FALSE -> JsonFalse
        // `JsonValue` has special handling that skips `START_OBJECT` prematurely
        END_OBJECT, FIELD_NAME -> JsonObjectDeserializer(p, context, useCurrentToken = true)
        else -> throw reportWrongTokenException(context, VALUE_FALSE)
      }
}


internal
class JsonScalarDeserializer : BaseDeserializer<JsonScalar>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonScalar =
      when (p.currentToken()) {
        VALUE_STRING -> JsonStringDeserializer(p, context)
        VALUE_NUMBER_INT -> JsonIntegralDeserializer(p, context)
        VALUE_NUMBER_FLOAT -> JsonFractionalDeserializer(p, context)
        VALUE_TRUE -> JsonTrue
        VALUE_FALSE -> JsonFalse
        else -> throw reportWrongTokenException(context, VALUE_FALSE)
      }
}

internal
class JsonCollectionDeserializer : BaseDeserializer<JsonCollection>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonCollection =
      when (p.currentToken()) {
        START_OBJECT -> JsonObjectDeserializer(p, context)
        START_ARRAY -> JsonArrayDeserializer(p, context)
        // `JsonValue` has special handling that skips `START_OBJECT` prematurely
        END_OBJECT, FIELD_NAME -> JsonObjectDeserializer(p, context, useCurrentToken = true)
        else -> throw reportWrongTokenException(context, START_ARRAY)
      }
}
