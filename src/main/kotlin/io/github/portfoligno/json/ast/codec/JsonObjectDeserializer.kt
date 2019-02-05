package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.*
import com.fasterxml.jackson.databind.DeserializationContext
import com.google.common.collect.ImmutableMap
import io.github.portfoligno.json.ast.JsonFalse
import io.github.portfoligno.json.ast.JsonNull
import io.github.portfoligno.json.ast.JsonObject
import io.github.portfoligno.json.ast.JsonTrue

private
fun <A, B> generateImmutableMap(nextFunction: () -> Pair<A, B>?): ImmutableMap<A, B> =
    ImmutableMap.copyOf(generateSequence(nextFunction).toMap())

internal
object JsonObjectDeserializer : ExpectedTokenDeserializer<JsonObject>(START_OBJECT) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonObject =
      JsonObject(generateImmutableMap {
        when (p.nextToken()) {
          FIELD_NAME -> p.currentName
          END_OBJECT -> null
          else -> throw reportWrongTokenException(context, JsonToken.END_OBJECT)
        }?.to(when (p.nextToken()) {
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
      })
}
