package io.github.portfoligno.json.ast.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.VALUE_EMBEDDED_OBJECT
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.util.TokenBuffer

internal
abstract class ExpectedTokenDeserializer<A>(private val expectedToken: JsonToken) : Deserializer<A>() {
  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): A =
      when (p.currentToken) {
        VALUE_EMBEDDED_OBJECT ->
          p.codec.readValue(p, TokenBuffer::class.java).asParser().run {
            if (nextToken() !== expectedToken) {
              throw reportWrongTokenException(ctxt, expectedToken)
            }
            invoke(this, ctxt)
          }
        expectedToken -> invoke(p, ctxt)
        else -> throw reportWrongTokenException(ctxt, expectedToken)
      }

  abstract operator fun invoke(p: JsonParser, context: DeserializationContext): A
}
