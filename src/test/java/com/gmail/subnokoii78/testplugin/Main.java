package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.tplcore.generic.MultiMap;
import com.gmail.subnokoii78.tplcore.parse.AbstractParser;
import org.jspecify.annotations.NullMarked;

import java.util.*;

public class Main {
    @NullMarked
    static class Parser extends AbstractParser<MultiMap<String, String>> {

        public Parser(String text) {
            super(text);
        }

        @Override
        protected Set<Character> getWhitespace() {
            return Set.of(' ');
        }

        @Override
        protected Set<Character> getQuotes() {
            return Set.of('"');
        }

        @Override
        protected Set<Character> getInvalidSymbolsInUnquotedString() {
            final Set<Character> symbols = new HashSet<>(super.getInvalidSymbolsInUnquotedString());
            symbols.remove(':');
            symbols.remove('.');
            symbols.remove('-');
            symbols.remove('!');
            return symbols;
        }

        @Override
        protected String getTrue() {
            return "true";
        }

        @Override
        protected String getFalse() {
            return "false";
        }

        @Override
        protected MultiMap<String, String> parse() {
            final var m = multiMap(
                '[', ']',
                ',', '=',
                key -> key,
                value -> value
            );
            finish();
            return m;
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello, world!");

        var p = new Parser("[type=!minecraft:player,nbt={SelectedItem:{id: 'minecraft:apple'}}]");

        System.out.println(p.parse());
    }
}
