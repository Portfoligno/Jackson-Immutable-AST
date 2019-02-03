package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.util.TokenBuffer

internal
typealias Deserializer<A> = com.fasterxml.jackson.databind.JsonDeserializer<A>

internal
fun Deserializer<*>.reportWrongTokenException(context: DeserializationContext, expectedToken: JsonToken): Throwable {
  context.reportWrongTokenException(this, expectedToken, null)
  throw AssertionError("Unexpected invocation")
}

internal
fun Deserializer<*>.reportInputMismatch(context: DeserializationContext, message: String): Throwable =
    context.reportInputMismatch<Throwable>(this, message)


internal
fun JsonParser.readValueAsTokens(): JsonParser =
    codec.readValue(this, TokenBuffer::class.java).asParser()
