package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import io.github.portfoligno.json.ast.Json

internal
object JsonSerializer : Serializer<Json>() {
  override
  fun serialize(value: Json, gen: JsonGenerator, serializers: SerializerProvider): Unit =
      value.toTokens(gen)
}
