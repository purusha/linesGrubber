package it.at.linesgrubber;

import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

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
                System.out.printf("Root directory must be a valid and readable directory: %s", root.getPath());
            }

            if (! proto.isFile() || ! proto.exists() || ! isReadable(proto)) {
                System.out.printf("Proto file must be a valid and readable file: %s", proto.getPath());
            }

            final ProtoParser parser = new ProtoParser(root, proto);

            final UUID uuid = UUID.randomUUID();



        }

        private boolean isReadable(File file) {
            return Files.isReadable(file.toPath());
        }
    }
}
