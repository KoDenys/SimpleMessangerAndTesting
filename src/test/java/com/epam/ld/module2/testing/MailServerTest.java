package com.epam.ld.module2.testing;

import com.epam.ld.module2.testing.template.Template;
import com.epam.ld.module2.testing.template.TemplateEngine;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(CustomRunnerExtension.class)
public class MailServerTest {
    MailServer mailServer;
    Template template;
    TemplateEngine templateEngine;
    Client client;
    Messenger messenger;

    private final String EMAIL = "abc@gmail.com";
    private final String NAME = "Den";
    private final String TEMPLATE_CONTENT = "Dear #{name} you are accepted!";
    private final String FILE_INPUT_NAME = "src/test/resources/input.txt";
    private final String FILE_OUTPUT_NAME = "src/test/resources/output.txt";
    private final String VARIABLES_KEY = "#{name}";
    private final String VARIABLES_VALUE = "Alex";
    private final String EXPECTED_MESSAGE = "abc@gmail.com\nDear Alex you are accepted!\nBest regards\nDen";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    public void initializationTest(){
        mailServer = new MailServer();
        template = new Template();
        templateEngine = new TemplateEngine();
        client = new Client();
        messenger = new Messenger(mailServer, templateEngine);
    }

    @OutputTest
    @Tag("FileTest")
    public void outputToFile_Should_Work_Correctly() throws IOException {
        //Given
        template.setContent(TEMPLATE_CONTENT);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(VARIABLES_KEY, VARIABLES_VALUE);
        client = new Client(EMAIL, NAME, expectedMap, FILE_INPUT_NAME, FILE_OUTPUT_NAME);
        String message = templateEngine.generateMessage(template, client);

        //When
        mailServer.send(client, message);

        //Then
        List<String> currentOutput = Files.readAllLines(Paths.get(FILE_OUTPUT_NAME));
        List<String> expectedOutput = Arrays.asList(EXPECTED_MESSAGE.split("\n"));
        assertLinesMatch(expectedOutput, currentOutput);
    }

    @OutputTest
    @Tag("ConsoleTest")
    public void outputToConsole_Should_Work_Correctly() {
        //Given
        template.setContent(TEMPLATE_CONTENT);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(VARIABLES_KEY, VARIABLES_VALUE);
        client = new Client(EMAIL, NAME, expectedMap, null,null);
        String message = templateEngine.generateMessage(template, client);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        //When
        mailServer.send(client, message);
        System.out.flush();
        System.setOut(old);
        String currentMessage = baos.toString().replaceAll("\r\n", "");

        //Then
        assertEquals(EXPECTED_MESSAGE, currentMessage);
    }

    @Test
    public void mockOutputToConsoleAndFile_Should_Send_Message_To_Both() throws IOException {
        //Given
        File tempFile= tempFolder.newFile(("file.txt"));
        MailServer mockMailServer = spy(MailServer.class);
        template = new Template(TEMPLATE_CONTENT);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(VARIABLES_KEY, VARIABLES_VALUE);
        client = new Client(EMAIL, NAME, expectedMap, FILE_INPUT_NAME, FILE_OUTPUT_NAME);
        templateEngine = new TemplateEngine();
        String message = templateEngine.generateMessage(template, client);
        StringBuilder tempMessage = new StringBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        //When
        doAnswer( (invocation) -> {
            mockMailServer.sendToConsole(message);
            Files.write(tempFile.toPath(),message.getBytes(StandardCharsets.ISO_8859_1));
            return null;
        }).when(mockMailServer).send(client, message);
        mockMailServer.send(client, message);
        System.out.flush();
        System.setOut(old);
        System.out.println("PATH : "+tempFile.toPath());
        String currentMessage = baos.toString().replaceAll("\r\n", "");

        List<String> tempInfo = Files.readAllLines(tempFile.toPath());
        for(String line : tempInfo){
            tempMessage.append(line).append("\n");
        }
        tempMessage.append("\r");

        //Then
        assertEquals(EXPECTED_MESSAGE, currentMessage);
        assertEquals(EXPECTED_MESSAGE, tempMessage.toString().replace("\n\r", ""));
    }
}
