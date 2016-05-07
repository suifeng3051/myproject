package com.zitech.gateway;

import com.zitech.gateway.gateway.PipeHelper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class MainTest {

    public static String compactJson(String json) {
        if (StringUtils.isEmpty(json))
            return "";

        char[] charArray = json.toCharArray();
        StringBuilder sb = new StringBuilder();

        int i = 0, count = 0, slash = 0;
        char c, b = 0;
        while (i < charArray.length) {
            c = charArray[i];
            switch (c) {
                case '"':
                case '\'':
                    if (count % 2 == 0) {
                        b = c;
                        ++count;
                    } else {
                        if (c == b) {
                            if (slash % 2 == 0) {
                                ++count;
                                b = 0;
                            } else {
                                ++slash; // tread \" as \\ to ++slash
                            }
                        }
                    }
                    sb.append(c);
                    break;
                case '\\':
                    ++slash;
                    sb.append(c);
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    if (count % 2 == 1)
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
