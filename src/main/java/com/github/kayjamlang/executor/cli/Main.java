package com.github.kayjamlang.executor.cli;

import com.github.kayjamlang.executor.Executor;
import com.github.kayjamlang.executor.MainLibrary;
import org.apache.commons.cli.*;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        Option file = new Option("f", "file", true, "KayJam file path");
        file.setRequired(true);
        options.addOption(file);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("kayjam", options);

            System.exit(1);
            return;
        }

        File executeFile = new File(Paths.get(System.getProperty("user.dir"),
                cmd.getOptionValue("file")).normalize().toString());

        Executor executor = new Executor();
        executor.addLibrary(new MainLibrary());
        executor.execute("{"+read(executeFile)+"}");
    }

    private static String read(File file) throws IOException {
        Scanner reader = new Scanner(file, UniversalDetector.detectCharset(file));

        StringBuilder value = new StringBuilder();
        while (reader.hasNextLine()) {
            value.append(reader.nextLine()).append("\n");
        }
        reader.close();


        //Filter
        value = new StringBuilder(value.toString().replaceAll("\r", "")
                .replaceAll("\t", ""));
        if (value.toString().startsWith("\uFEFF")) {
            value = new StringBuilder(value.substring(1));
        }

        return value.toString();
    }
}
