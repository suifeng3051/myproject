package com.zitech.gateway;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

public class MainTest {
    public static String compactJson(String json) {
        if (StringUtils.isEmpty(json))
            return "";

        char[] charArray = json.toCharArray();
        StringBuilder sb = new StringBuilder();
        Stack<Integer> stack = new Stack<>();

        int i = 0;
        char c;
        while (i < charArray.length) {
            c = charArray[i];
            switch (c) {
                case '"':
                case '\'':
                case '\\':
                    if (stack.size() % 2 == 1) {
                        stack.push(1);
                        sb.append(c);
                    }
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    if (stack.size() % 2 == 1)
                        sb.append(c);
                    break;
                default: {
                    sb.append(c);
                }
            }
            ++i;
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        FileInputStream fisStruct = new FileInputStream("/Users/bobo/Desktop/json.json");
        String s = IOUtils.toString(fisStruct);
        s = compactJson(s);
        System.out.println(s);
    }
}
