package com.gmail.subnokoii78.util.file.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JSONParser {
    private final String text;

    private final Map<String, Object> map = new LinkedHashMap<>();

    private int location = 0;

    public JSONParser(@NotNull String json) {
        this.text = json.replaceAll("\n", "");
    }

    public JSONObject parseObject() {
        reset();
        parseObjectCommon();
        remainingChars();
        return new JSONObject(map);
    }

    public JSONArray parseArray() {
        reset();
        if (next() != '[') {
            throw new IllegalStateException("配列は[で開始される必要があります");
        }

        if (array() instanceof List<Object> list) {
            remainingChars();
            return new JSONArray(list);
        }
        else throw new IllegalArgumentException("配列ではありません");
    }

    private void reset() {
        map.clear();
        location = 0;
    }

    private void back() {
        location--;
    }

    private char next() {
        if (location == text.length()) {
            throw new IllegalStateException("文字列の長さが期待より不足しています");
        }

        final char next = text.charAt(location++);

        if (next == ' ') {
            return next();
        }

        return next;
    }

    private char nextIncludesWhiteSpace() {
        if (location == text.length()) {
            throw new IllegalStateException("文字列の長さが期待より不足しています");
        }

        return text.charAt(location++);
    }

    private Object element() {
        final char current = next();
        return switch (current) {
            case '{' -> object();
            case '[' -> array();
            case '"' -> string();
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> number(current);
            case 't', 'f' -> bool(current);
            case 'n' -> _null();
            default -> throw new IllegalStateException("要素から次の要素への移動中に無効な文字を検知しました: " + current);
        };
    }

    private JSONParser parseObjectCommon() {
        if (next() != '{') {
            throw new IllegalStateException("オブジェクトは{で開始される必要があります");
        }

        final char current = next();

        switch (current) {
            case '}':
                break;
            case '"':
                key();
                char expectedBraceEnd = next();
                if (expectedBraceEnd != '}') {
                    throw new IllegalStateException("オブジェクトの解析中に無効な文字を検知しました: " + expectedBraceEnd);
                }
                break;
            default:
                throw new IllegalStateException("オブジェクトの解析中に無効な文字を検知しました: " + current);
        }

        return this;
    }

    private void remainingChars() {
        while (text.length() > location) {
            if (text.charAt(location++) != ' ') {
                throw new IllegalStateException("json文字列の後に無効な文字を検知しました: " + text.charAt(location - 1));
            }
        }
    }

    private void key() {
        final String key = string();
        final char current = next();
        if (current == ':') {
            map.put(key, element());
            final char commaOrBrace = next();
            if (commaOrBrace == ',') {
                final char quote = next();
                if (quote == '"') key();
                else throw new IllegalStateException("ペアの解析中に無効な文字を検知しました: " + quote);
            }
            else if (commaOrBrace == '}') {
                back();
            }
            else {
                throw new IllegalStateException("閉じ括弧が見つかりません");
            }
        }
        else throw new IllegalStateException("ペアの解析中に無効な文字を検知しました: " + current);
    }

    private Map<String, Object> object() {
        var object = new JSONParser(text.substring(location - 1)).parseObjectCommon();
        location += object.location - 1;
        return object.map;
    }

    private List<Object> array() {
        var list = new ArrayList<>();
        var next = next();

        if (next == ']') {
            return list;
        }

        back();

        do {
            list.add(element());
            next = next();
        }
        while (next == ',');

        if (next == ']') return list;
        else throw new IllegalStateException("配列は]で終了される必要があります");
    }

    private String string() {
        char previous = '"';
        char current = nextIncludesWhiteSpace();
        StringBuilder str = new StringBuilder();
        while (true) {
            if (previous != '\\' && current == '"') {
                break;
            }
            else if (previous == '\\' && current == '"') {
                str.delete(str.length() - 1, str.length());
            }

            str.append(current);

            previous = current;
            current = nextIncludesWhiteSpace();
        }

        return str.toString();
    }

    private double number(char start) {
        char previous = start;
        char current = next();
        boolean includesDot = false;
        StringBuilder str = new StringBuilder();
        str.append(previous);
        while (true) {
            if ("0123456789".contains(String.valueOf(current))) {
                str.append(current);
            }
            else if ("0123456789".contains(String.valueOf(previous)) && current == '.' && !includesDot) {
                str.append(current);
                includesDot = true;
            }
            else if (current == ',' || current == '}' || current == ']') {
                back();
                break;
            }
            else throw new IllegalStateException("数値の解析中に無効な文字を検知しました: " + current);

            previous = current;
            current = next();
        }

        return Double.parseDouble(str.toString());
    }

    private boolean bool(char start) {
        StringBuilder str = new StringBuilder();
        str.append(start);
        while (true) {
            str.append(next());

            String built = str.toString();

            IllegalStateException e = new IllegalStateException("真偽値の解析中に無効な文字を検知しました: " + built.charAt(built.length() - 1));
            if (built.startsWith("true")) {
                if (built.equals("true")) return true;
                else throw e;
            }
            else if (built.startsWith("false")) {
                if (built.equals("false")) return false;
                else throw e;
            }
        }
    }

    private @Nullable Object _null() {
        char u = next();
        char l = next();
        char l_2 = next();
        if (u != 'u') throw new IllegalStateException("null値の解析中に無効な文字を検知しました: " + u);
        if (l != 'l') throw new IllegalStateException("null値の解析中に無効な文字を検知しました: " + l);
        if (l_2 != 'l') throw new IllegalStateException("null値の解析中に無効な文字を検知しました: " + l_2);
        return null;
    }
}
