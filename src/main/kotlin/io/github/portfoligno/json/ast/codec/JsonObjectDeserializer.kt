package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.*
import com.fasterxml.jackson.databind.DeserializationContext
import com.google.common.collect.ImmutableMap
import io.github.portfoligno.json.ast.*

internal
object JsonObjectDeserializer : ExpectedTokenDeserializer<JsonObject>(START_OBJECT) {
  override
  operator fun invoke(p: JsonParser, context: DeserializationContext): JsonObject {
    val elements = ImmutableMap.builder<String, Json>()

    apply loop@{
      while (true) {
        elements.put(
            when (p.nextToken()) {
              FIELD_NAME -> p.currentName
              END_OBJECT -> return@loop
              else -> throw reportWrongTokenException(context, JsonToken.END_OBJECT)
            },
            when (p.nextToken()) {
              START_OBJECT -> invoke(p, context)
              START_ARRAY -> JsonArrayDeserializer(p, context)
              VALUE_EMBEDDED_OBJECT -> JsonDeserializer.fromEmbedded(p, context)
              VALUE_STRING -> JsonStringDeserializer(p, context)
              VALUE_NUMBER_INT -> JsonIntegralDeserializer(p, context)
              VALUE_NUMBER_FLOAT -> JsonFractionalDeserializer(p, context)
              VALUE_TRUE -> JsonTrue
              VALUE_FALSE -> JsonFalse
              VALUE_NULL -> JsonNull
              else -> throw reportWrongTokenException(context, JsonToken.VALUE_NULL)
            })
      }
    }
    return JsonObject(elements.build())
  }
}
