package it.at.linesgrubber;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

    public static void main( String[] args ) {
        int exitCode = new CommandLine(new GrubberCommand()).execute(args);

        System.exit(exitCode);
    }

    @CommandLine.Command(name = "grub", description = "Grub folder using proto file")
    static class GrubberCommand implements Runnable {

        @CommandLine.Option(names = { "-r", "--root" }, description = "the root directory", required = true)
        File root;

        @CommandLine.Option(names = { "-p", "--proto" }, description = "the proto file", required = true)
        File proto;

//        @CommandLine.Parameters(index = "0", defaultValue = "stranger", description = "The name for greeting")
//        private String name;

        @Override
        public void run() {
            if (! root.isDirectory() || ! root.exists() || ! isReadable(root)) {
                System.out.println("Root directory must be a valid and readable directory: " + root.getPath());
            }

            if (! proto.isFile() || ! proto.exists() || ! isReadable(proto)) {
                System.out.println("Proto file must be a valid and readable file: " + proto.getPath());
            }

            final ProtoParser parser = new ProtoParser(root, proto);

            File file = null;
            try {
                final UUID uuid = UUID.randomUUID();
                file = Paths.get(uuid.toString()).toFile();
                boolean newFile = file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Grab data using these configuration:");
            parser.getData()
                .forEach(e -> {
                    System.out.println(e.toString());
                });

            System.out.println("Write collected data on: " + file.getAbsolutePath());
            process(parser.getData(), file);
        }

        private void process(List<ProtoParser.FileRange> data, File file) {
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
                Files.write(
                    file.toPath(),
                    Files.readAllLines(fr.getFile())
                );
            } else {
                for(Range<Integer> r : fr.getRanges()) {
                    Files.write(
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
    }

    public static boolean isReadable(File file) {
        return Files.isReadable(file.toPath());
    }

}
