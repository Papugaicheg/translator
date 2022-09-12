package com.tinkoff.translator.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IAMTokenRequester {
    public static String excCommand() {
        Process process;
        try {
            process = Runtime.getRuntime()
                    .exec(String.format("cmd.exe /c %s", "yc iam create-token"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String result = " ";
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result = line;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}

