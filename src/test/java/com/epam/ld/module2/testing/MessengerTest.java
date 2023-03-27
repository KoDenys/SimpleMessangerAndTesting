package com.epam.ld.module2.testing;

import com.epam.ld.module2.testing.template.Template;
import com.epam.ld.module2.testing.template.TemplateEngine;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(CustomRunnerExtension.class)
public class MessengerTest {
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
    private final String VARIABLES = "#{name}=Alex;";
    private final String VARIABLES_KEY = "#{name}";
    private final String VARIABLES_VALUE = "Alex";
    private final String EXPECTED_INPUT = "abc@gmail.com\nDen\nDear #{name} you are accepted!\n#{name}=Alex;";

    @Rule
    public ExpectedException expectedException= ExpectedException.none();

    @BeforeEach
    public void initializationTest(){
        mailServer = new MailServer();
        template = new Template();
        templateEngine = new TemplateEngine();
        client = new Client();
        messenger = new Messenger(mailServer, templateEngine);
    }

    @InputTest
    @Tag("ConsoleTest")
    public void inputFromConsoleTest_Should_Work_Correctly(){
        //Given
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(VARIABLES_KEY, VARIABLES_VALUE);
        Client clientExpected = new Client(EMAIL, NAME, expectedMap, null, null);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(EXPECTED_INPUT.getBytes());
        System.setIn(byteArrayInputStream);

        //When
        messenger.inputDataFromConsole(template, client);

        //Then
        assertEquals(clientExpected, client);
        assertEquals(TEMPLATE_CONTENT, template.getContent());
    }

    @InputTest
    @Tag("ConsoleTest")
    public void mockInputFromConsoleTest_Should_Work_Correctly(){
        //Given
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(VARIABLES_KEY, VARIABLES_VALUE);
        Client clientExpected = new Client(EMAIL, NAME, expectedMap, null, null);
        Messenger mockMessenger = mock(Messenger.class);

        //When
        doAnswer( (invocation) -> {
            template.setContent(TEMPLATE_CONTENT);
            client.setAddresses(EMAIL);
            client.setName(NAME);
            client.setVariables(expectedMap);
            return null;
        }).when(mockMessenger).inputDataFromConsole(template, client);

        mockMessenger.inputDataFromConsole(template, client);

        //Then
        assertEquals(clientExpected, client);
        assertEquals(TEMPLATE_CONTENT, template.getContent());
    }

    @InputTest
    @Tag("FileTest")
    public void mockInputFromFileTest_Should_Work_Correctly(){
        //Given
        Messenger mockMessenger = mock(Messenger.class);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(VARIABLES_KEY, VARIABLES_VALUE);
        Client clientExpected = new Client(EMAIL, NAME, expectedMap, FILE_INPUT_NAME, FILE_OUTPUT_NAME);
        client.setInputFileName(FILE_INPUT_NAME);
        client.setOutputFileName(FILE_OUTPUT_NAME);

        //When
        doAnswer( (invocation) -> {
            template.setContent(TEMPLATE_CONTENT);
            client.setAddresses(EMAIL);
            client.setName(NAME);
            client.setVariables(expectedMap);
            return null;
        }).when(mockMessenger).inputDataFromFile(template, client);

        mockMessenger.inputDataFromFile(template, client);

        //Then
        assertEquals(clientExpected, client);
        assertEquals(TEMPLATE_CONTENT, template.getContent());
    }

    @InputTest
    @Tag("FileTest")
    @DisabledOnOs({OS.LINUX, OS.MAC})
    public void inputFromFileTest_Should_Work_Correctly(){
        //Given
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(VARIABLES_KEY, VARIABLES_VALUE);
        Client clientExpected = new Client(EMAIL, NAME, expectedMap, FILE_INPUT_NAME, FILE_OUTPUT_NAME);
        client.setInputFileName(FILE_INPUT_NAME);
        client.setOutputFileName(FILE_OUTPUT_NAME);

        //When
        messenger.inputDataFromFile(template, client);

        //Then
        assertEquals(clientExpected, client);
        assertEquals(TEMPLATE_CONTENT, template.getContent());
    }

    @InputTest
    @Tag("FileTest")
    public void expectedIOException(){
        //Given
        client.setInputFileName("");

        //When
        messenger.inputDataFromFile(template, client);

        //Then
        expectedException.expect(IOException.class);
    }

    @Test
    public void putVariablesToMapTest(){
        //Given
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(VARIABLES_KEY, VARIABLES_VALUE);

        //When
        Map<String, String> map = messenger.putVariablesToMap(VARIABLES);

        //Then
        assertEquals(expectedMap, map);
    }
}
