package it.at.linesgrubber;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Getter
public class ProtoParser {

    private static final char V = ',';

    private final List<FileRange> data;

    public ProtoParser(File root, File proto) {

        final List<FileRange> tmp;

        //step1: extract path
        try (Stream<String> lines = java.nio.file.Files.lines(proto.toPath())) {
            tmp = lines.map(line -> {
                if (StringUtils.isNotBlank(line) && !StringUtils.contains(line, V)) {
                    final Optional<Path> path = extractPath(root, line);

                    if (path.isPresent()) {
                        return new FileRange(path.get());
                    }
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
            final AtomicInteger counter = new AtomicInteger(-1);

            lines.forEach(line -> {
                if (StringUtils.isNotBlank(line)) {
                    if (StringUtils.contains(line, V)) {
                        final int index = counter.get();

                        extractRange(line)
                            .ifPresent(r -> tmp.get(index).add(r));
                    } else {
                        counter.incrementAndGet();
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //step3: merge overlapped ranges (if exists)
        this.data = tmp.stream()
            .map(fr -> {
                final FileRange fileRange = new FileRange(fr.getFile());

                mergeRanges(fr.getRanges())
                    .forEach(fileRange::add);

                return fileRange;
            })
            .toList();
    }

    private List<Range<Integer>> mergeRanges(List<Range<Integer>> ranges) {
        if (ranges.isEmpty()) {
            return new ArrayList<>();
        }

        // Ordina i range per punto di inizio
        ranges.sort(Comparator.comparingInt(Range::getMinimum));

        final List<Range<Integer>> merged = new ArrayList<>();
        Range<Integer> current = ranges.get(0);

        for (int i = 1; i < ranges.size(); i++) {
            Range<Integer> next = ranges.get(i);

            // Se c'è overlap o sono adiacenti
            if (current.getMaximum() >= next.getMinimum()) {
                // Merge: estendi il range corrente
                current = Range.of(current.getMinimum(), Math.max(current.getMaximum(), next.getMaximum()));
            } else {
                // Nessun overlap: aggiungi il range corrente e vai al prossimo
                merged.add(current);
                current = next;
            }
        }

        // Aggiungi l'ultimo range
        merged.add(current);

        return merged;
    }

    private Optional<Path> extractPath(File root, String line) {
        final Path path = Paths.get(root.getAbsolutePath() + "/" + StringUtils.trim(line));
        final File file = path.toFile();

        if (! file.exists() || ! file.isFile() || ! App.isReadable(file)) {
            return Optional.empty();
        }

        return Optional.of(path);
    }

    private Optional<Range<Integer>> extractRange(String line) {
        final int countOf = StringUtils.countMatches(line, V);

        if (countOf > 1) {
            return Optional.empty();
        }

        final String[] values = StringUtils.split(line, V);
        Range<Integer> range = null;

        if (values.length >= 1) {
            if (values.length == 1) {
                final String start = StringUtils.trim(values[0]);

                if (StringUtils.isNumeric(start)) {
                    range = Range.of(Integer.parseInt(start), Integer.MAX_VALUE);
                }
            } else {
                final String start = StringUtils.trim(values[0]);
                final String end = StringUtils.trim(values[1]);

                if (StringUtils.isNumeric(start) && StringUtils.isNumeric(end)) {
                    final int i1 = Integer.parseInt(start);
                    final int i2 = Integer.parseInt(end);

                    if (i1 <= i2) {
                        range = Range.of(i1, i2);
                    }
                }
            }
        }

        return Optional.ofNullable(range);
    }

    @ToString
    @RequiredArgsConstructor
    @Getter
    public static final class FileRange {
        private final Path file;
        private final List<Range<Integer>> ranges = new ArrayList<>();

        public void add(Range<Integer> range) {
            ranges.add(range);
        }
    }

}
