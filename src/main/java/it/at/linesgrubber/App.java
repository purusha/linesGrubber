package it.at.linesgrubber;

import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            new ProtoHandler().process(parser.getData(), file);
        }
    }

    public static boolean isReadable(File file) {
        return Files.isReadable(file.toPath());
    }

}
