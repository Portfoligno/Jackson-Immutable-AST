package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.VALUE_EMBEDDED_OBJECT
import com.fasterxml.jackson.databind.DeserializationContext

internal
abstract class BaseDeserializer<A> : Deserializer<A>() {
  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): A =
      when (p.currentToken) {
        VALUE_EMBEDDED_OBJECT -> invoke(p.readValueAsTokens(), ctxt)
        else -> invoke(p, ctxt)
      }

  abstract operator fun invoke(p: JsonParser, context: DeserializationContext): A
}

internal
abstract class ExpectedTokenDeserializer<A>(private val expectedToken: JsonToken) : BaseDeserializer<A>() {
  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): A =
      when (p.currentToken) {
        VALUE_EMBEDDED_OBJECT ->
          p.readValueAsTokens().let {
            if (it.nextToken() !== expectedToken) {
              throw reportWrongTokenException(ctxt, expectedToken)
            }
            invoke(it, ctxt)
          }
        expectedToken -> invoke(p, ctxt)
        else -> throw reportWrongTokenException(ctxt, expectedToken)
      }
}
