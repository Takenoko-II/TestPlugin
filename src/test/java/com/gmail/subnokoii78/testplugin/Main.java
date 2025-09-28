package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.tplcore.parse.AbstractParser;

import java.util.*;

public class Main {
    static class Parser extends AbstractParser<String[]> {

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
        protected String getTrue() {
            return "true";
        }

        @Override
        protected String getFalse() {
            return "false";
        }

        @Override
        protected String[] parse() {
            ignore();
            if (isOver()) {
                return null;
            }

            final List<String> arguments = new ArrayList<>();

            expect(true, '[');

            do {
                var s = string(false, '=');

                expect(true, '=');

                final boolean not = next(false, '!') != null;

                if (not) {
                    arguments.add(String.format("{%s=!%s}", s, number(false)));
                }
                else {
                    System.out.println(peek(false));
                    arguments.add(String.format("{%s=%s}", s, number(false)));
                    System.out.println(peek(false));
                }
            }
            while (next(true, ',') != null);

            expect(true, ']');

            finish();

            return arguments.toArray(String[]::new);
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello, world!");

        var p = new Parser("[a=1.7,b=!2,c=3]");

        System.out.println(Arrays.toString(p.parse()));
    }
}
