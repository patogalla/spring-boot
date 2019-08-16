package com.patogalla.api.utils.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.springframework.core.convert.converter.Converter;

import javax.persistence.Embeddable;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@JsonSerialize(using = Identity.IdentitySerializer.class)
@JsonDeserialize(using = Identity.IdentityDeserializer.class)
public class Identity implements Serializable {

    @Type(type = "pg-uuid") UUID value;

    Identity() {}

    public Identity(UUID value) {
        this.value = value;
    }

    public String stringValue() {
        return value.toString();
    }

    public BigInteger bigIntegerValue() {
        return new BigInteger(asByteArray());
    }

    private byte[] asByteArray() {
        return ByteBuffer
                .allocate(16)
                .putLong(value.getMostSignificantBits())
                .putLong(value.getLeastSignificantBits())
                .array();
    }

    public static <E> Identity random() {
        return new Identity(UUID.randomUUID());
    }

    public static Identity unsafeFrom(final UUID uuid) {
        return new Identity(Objects.requireNonNull(uuid));
    }

    public static Identity unsafeFromString(final String uuidString) {
        return new Identity(UUID.fromString(Objects.requireNonNull(uuidString)));
    }

    public static class IdentityConverter implements Converter<String, Identity> {

        @Override
        public Identity convert(String source) {
            return Identity.unsafeFromString(source);
        }
    }

    public static class IdentitySerializer extends JsonSerializer<Identity> {

        @Override
        public void serialize(Identity identity, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(identity.value.toString());
        }
    }

    public static class IdentityDeserializer extends JsonDeserializer<Identity> {
        @Override
        public Identity deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            final ObjectCodec oc = jsonParser.getCodec();
            final JsonNode node = oc.readTree(jsonParser);           //Unsafe
            return new Identity(UUID.fromString(node.textValue()));  //Unsafe
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identity identity = (Identity) o;
        return value.equals(identity.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Identity{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
