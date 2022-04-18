package com.luka5w.customplants.common.data;

import java.io.*;
import java.util.ArrayList;

public class FileUtils {
    public static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) builder.append(str).append("\n");
        return builder.toString();
    }
    
    public static ArrayList<String> readFileAsList(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArrayList<String> builder = new ArrayList<>();
        String str;
        while ((str = reader.readLine()) != null) builder.add(str);
        return builder;
    }
}
