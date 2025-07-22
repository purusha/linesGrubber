package it.at.linesgrubber;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ProtoParser {

    //must be immutable
    private static final class FileRange {
        private final String file;
        private final List<Range<Integer>> ranges = new ArrayList<>();

        public FileRange(String file) {
            this.file = file;
        }

        public void add(Range<Integer> range) {
            ranges.add(range);
        }

        public String getFile() {
            return file;
        }

        public List<Range<Integer>> getRanges() {
            return Collections.unmodifiableList(ranges);
        }
    }

    private final List<FileRange> data;

    public ProtoParser(File root, File proto) {

        //step1: extract path
        try (Stream<String> lines = java.nio.file.Files.lines(proto.toPath())) {
            this.data = lines.map(line -> {
                if (StringUtils.isNotBlank(line) && !StringUtils.contains(line, ",")) {
                    return new FileRange(extractPath(root, line));
                }

                return null;
            })
            .filter(Objects::nonNull)
            .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //step2: extract ranges
        try (Stream<String> lines = java.nio.file.Files.lines(proto.toPath())) {
            final AtomicInteger counter = new AtomicInteger(0);

            lines.forEach(line -> {
                final int index = counter.getAndIncrement();

                if (StringUtils.isNotBlank(line) && StringUtils.contains(line, ",")) {
                    extractRange(line)
                        .ifPresent(r -> data.get(index).add(r));
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String extractPath(File root, String line) {
        return "";
    }

    private Optional<Range<Integer>> extractRange(String line) {
        return Optional.empty();
    }

}
