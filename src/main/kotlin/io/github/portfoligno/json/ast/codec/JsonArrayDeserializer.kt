package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.*
import com.fasterxml.jackson.databind.DeserializationContext
import com.google.common.collect.ImmutableList
import io.github.portfoligno.json.ast.*

internal
object JsonArrayDeserializer : ExpectedTokenDeserializer<JsonArray>(START_ARRAY) {
  override
  operator fun invoke(p: JsonParser, context: DeserializationContext): JsonArray {
    val elements = ImmutableList.builder<Json>()

    apply loop@{
      while (true) {
        elements.add(when (p.nextToken()) {
          END_ARRAY -> return@loop
          START_OBJECT -> JsonObjectDeserializer(p, context)
          START_ARRAY -> invoke(p, context)
          VALUE_EMBEDDED_OBJECT -> JsonDeserializer.fromEmbedded(p, context)
          VALUE_STRING -> JsonStringDeserializer(p, context)
          VALUE_NUMBER_INT -> JsonIntegralDeserializer(p, context)
          VALUE_NUMBER_FLOAT -> JsonFractionalDeserializer(p, context)
          VALUE_TRUE -> JsonTrue
          VALUE_FALSE -> JsonFalse
          VALUE_NULL -> JsonNull
          else -> throw reportWrongTokenException(context, END_ARRAY)
        })
      }
    }
    return JsonArray(elements.build())
  }
}
