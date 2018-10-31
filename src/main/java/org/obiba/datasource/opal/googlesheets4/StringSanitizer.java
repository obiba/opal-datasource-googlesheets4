package org.obiba.datasource.opal.googlesheets4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class StringSanitizer {

  private final static String ID_PATTERN = "^([a-zA-Z0-9-_]+)$";
  private final static String URL_PATTERN = "\\/spreadsheets\\/d\\/([a-zA-Z0-9-_]+)\\/";

  static String sanitize(String[] value) {
    return Stream.of(value).map(StringSanitizer::sanitize).collect(Collectors.joining(","));
  }

  static String sanitize(String value) {
    return String.format("\"%s\"", value.trim()
      .replace("\"", "")
      .replaceAll("\\([^\\)]*\\)", "")); // function args
  }

  static String unquote(String value) {
    return value.replaceAll("['\"]", "");
  }

  static String sanitizeId(String value) {
    String id = extractId(URL_PATTERN, value);
    return "".equals(id) ? extractId(ID_PATTERN, value) : id;
  }

  private static String extractId(String regexp, String value) {
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(value);
    if (matcher.find()) {
      return matcher.group(1);
    }

    return "";
  }
}
