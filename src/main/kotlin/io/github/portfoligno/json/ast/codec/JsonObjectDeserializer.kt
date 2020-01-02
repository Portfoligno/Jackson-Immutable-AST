package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.*
import com.fasterxml.jackson.databind.DeserializationContext
import com.google.common.collect.ImmutableMap
import io.github.portfoligno.json.ast.JsonFalse
import io.github.portfoligno.json.ast.JsonNull
import io.github.portfoligno.json.ast.JsonObject
import io.github.portfoligno.json.ast.JsonTrue

@Suppress("UNCHECKED_CAST")
private
fun <T> Sequence<T?>.takeWhileNotNull(): Sequence<T> =
    takeWhile { it != null } as Sequence<T>

internal
object JsonObjectDeserializer : BaseDeserializer<JsonObject>() {
  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): JsonObject =
      when (p.currentToken) {
        START_OBJECT -> invoke(p, ctxt)
        VALUE_EMBEDDED_OBJECT -> invoke(p.currentValueTokens(ctxt), ctxt)
        // `JsonValue` has special handling that skips `START_OBJECT` prematurely
        END_OBJECT, FIELD_NAME -> invoke(p, ctxt, useCurrentToken = true)
        else -> invoke(p, ctxt)
      }

  override
  fun invoke(p: JsonParser, context: DeserializationContext): JsonObject =
      invoke(p, context, useCurrentToken = false)

  operator fun invoke(p: JsonParser, context: DeserializationContext, useCurrentToken: Boolean): JsonObject =
      generateSequence(useCurrentToken) { false }
          .map {
            when (if (it) p.currentToken else p.nextToken()) {
              FIELD_NAME -> p.currentName
              END_OBJECT -> null
              else -> throw reportWrongTokenException(context, END_OBJECT)
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
              else -> throw reportWrongTokenException(context, VALUE_NULL)
            })
          }
          .takeWhileNotNull()
          .let { JsonObject(ImmutableMap.copyOf(it.toMap())) }
}
