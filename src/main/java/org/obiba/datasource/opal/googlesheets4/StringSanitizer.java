package org.obiba.datasource.opal.googlesheets4;

import java.util.stream.Collectors;
import java.util.stream.Stream;

final class StringSanitizer {

    static String sanitize(String[] value) {
        return Stream.of(value).map(StringSanitizer::sanitize).collect(Collectors.joining(","));
    }

    static String sanitize(String value) {
        return String.format("\"%s\"", value.trim().replace("\"", ""));
    }

    static String unquote(String value) {
        return value.replaceAll("['\"]", "");
    }
}
