package it.at.linesgrubber;

import org.apache.commons.lang3.Range;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class ProtoHandler {

    public void process(List<ProtoParser.FileRange> data, File file) {
        System.out.println("Grab data using these configuration:");
        data
            .forEach(e -> {
                System.out.println(e.toString());
            });

        System.out.println("Write collected data on: " + file.getAbsolutePath());
        data.forEach(e -> {
            try {
                append(e, file);
            } catch (IOException ex) {
                System.out.println("Error during collection data on: " + e.getFile());
            }
        });
    }

    private void append(ProtoParser.FileRange fr, File file) throws IOException {
        if(fr.getRanges().isEmpty()) {
            write(
                file.toPath(),
                Files.readAllLines(fr.getFile())
            );
        } else {
            for(Range<Integer> r : fr.getRanges()) {
                write(
                    file.toPath(),
                    Files
                        .lines(fr.getFile(), StandardCharsets.UTF_8)
                        .skip(r.getMinimum() - 1)
                        .limit(r.getMaximum() - r.getMinimum() + 1)
                        .collect(Collectors.toList())
                );
            }
        }
    }

    private void write(Path path, Iterable<? extends CharSequence> lines) throws IOException {
        Files.write(
            path, "\n".getBytes(), StandardOpenOption.APPEND
        );

        Files.write(
            path, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND
        );
    }

}
