package it.at.linesgrubber;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ProtoParserTest {

    @TempDir(cleanup = CleanupMode.NEVER)
    public File root;

    @BeforeEach
    void setUp() throws IOException {
        new File(root, "path/file/").mkdirs();
        new File(root, "path/file/n1.java").createNewFile();
        new File(root, "path/huge/file/").mkdirs();
        new File(root, "path/huge/file/n2.java").createNewFile();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test1() throws URISyntaxException {
        URL resource = getClass().getResource("/test1");
        assertNotNull(resource);

        runEvaluation(resource);
    }

    @Test
    void test2() throws URISyntaxException {
        URL resource = getClass().getResource("/test2");
        assertNotNull(resource);

        runEvaluation(resource);
    }

    @Test
    void test3() throws URISyntaxException {
        URL resource = getClass().getResource("/test3");
        assertNotNull(resource);

        runEvaluation(resource);
    }

    /*
        no test under
     */

    private void runEvaluation(URL resource) throws URISyntaxException {
        Path filePath = Paths.get(resource.toURI());
        log.info("{}", filePath);

        ProtoParser parser = new ProtoParser(root, filePath.toFile());

        List<ProtoParser.FileRange> data = parser.getData();
        log.info("{}", data.size());

        data.stream()
            .forEach(e -> {
                log.info("{}", e);
            });
    }
}