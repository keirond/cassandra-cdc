package com.fujitsu.fnc.vta.cassandra_cdc.common;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.jackson.JsonComponentModule;

import java.util.Optional;

/**
 * A utility class for converting objects to JSON strings and vice versa using Jackson.
 */
public class JsonMapper {

    private static final Logger log = LogManager.getLogger(JsonMapper.class);

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper().registerModule(new Jdk8Module())
                               .registerModule(new JavaTimeModule())
                               .registerModule(new ParameterNamesModule())
                               .registerModule(new JsonComponentModule())
                               .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                               .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                               .setSerializationInclusion(Include.NON_NULL);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private JsonMapper() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    /**
     * Converts an object to its JSON string representation.
     *
     * @param object the object to convert
     *
     * @return the JSON string, or an empty JSON object (`{}`) if the object is null
     *
     * @exception RuntimeException if serialization fails
     */
    @SneakyThrows
    public static String toJson(Object object) {
        if (object == null) return "{}";
        return object instanceof String string ? string : Optional.ofNullable(
                objectMapper.writeValueAsString(object)).orElse("{}");
    }

    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param object the JSON string
     * @param type   the class of the object to convert to
     * @param <T>    the type of the object
     *
     * @return the converted object
     *
     * @exception RuntimeException if deserialization fails
     */
    @SneakyThrows
    public static <T> T fromString(String object, Class<T> type) {
        if (type.getTypeName().equals(String.class.getTypeName())) {
            return (T) object;
        }
        return objectMapper.readValue(object, type);
    }

    /**
     * Safely converts a JSON string to an {@link Optional} of the specified type. Returns an empty
     * {@link Optional} if the input is null, empty, or invalid.
     *
     * @param object the JSON string
     * @param type   the class of the object to convert to
     * @param <T>    the type of the object
     *
     * @return an {@link Optional} containing the converted object, or empty if deserialization
     * fails
     */
    @SneakyThrows
    public static <T> Optional<T> fromStringOpt(String object, Class<T> type) {
        try {
            if (StringUtils.isEmpty(object) || "null".equals(object)) {
                return Optional.empty();
            }
            return Optional.of(fromString(object, type));
        } catch (Exception e) {
            log.error("parse json failed: {}", object, e);
            return Optional.empty();
        }
    }

}