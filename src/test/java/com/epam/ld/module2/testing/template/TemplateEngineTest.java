package com.epam.ld.module2.testing.template;

import com.epam.ld.module2.testing.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(CustomRunnerExtension.class)
public class TemplateEngineTest {
    MailServer mailServer;
    TemplateEngine templateEngine;
    Template template;
    Messenger messenger;
    Client client;

    private static final String TEMPLATE_EVENT = "Dear, #{name} you are invited to #{event}. Location is #{location}";
    private static final String TEMPLATE_EVENT_EXPECTED = "Dear, Alex you are invited to Ibiza Party. Location is Ibiza Beach Club";

    @BeforeEach
    public void initializationTest(){
        mailServer = new MailServer();
        templateEngine = new TemplateEngine();
        template = new Template();
        messenger = new Messenger(mailServer, templateEngine);
        client = new Client();
    }

    @ParameterizedTest
    @ValueSource(strings = "#{name}=Alex; #{event}=Ibiza Party;")
    public void withoutOnePlaceholderValue_Should_Return_Exception(String vars){
        //Given
        template.setContent(TEMPLATE_EVENT);
        client.setVariables(messenger.putVariablesToMap(vars));

        //When
        Executable executable = () -> templateEngine.checkVariablesInTemplate(template, client);

        //Then
        assertThrows(InvalidParameterException.class, executable);
    }

    @TestFactory
    Stream<DynamicTest> withReplacePlaceholder_Should_Work_Correctly(){
        List<String> variables = Arrays.asList("#{name}=Alex; #{event}=Ibiza Party; #{location}=Ibiza Beach Club;",
                "#{name}=Alex; #{event}=Ibiza Party; #{location}=Ibiza Beach Club; #{other}=other value;",
                "#{name}=Alex; #{event}=Ibiza Party; #{location}=Ibiza Beach Club; other value;",
                "#{name}=#{firstName}; #{event}=Ibiza Party; #{location}=Ibiza Beach Club; #{other}=#{other value};",
                "#{name}=#{fullName}; #{event}=Ibiza Party; #{location}=#{eventLocation};");

        List<String> expectedResult = Arrays.asList(TEMPLATE_EVENT_EXPECTED, TEMPLATE_EVENT_EXPECTED, TEMPLATE_EVENT_EXPECTED,
                "Dear, #{firstName} you are invited to Ibiza Party. Location is Ibiza Beach Club",
                "Dear, #{fullName} you are invited to Ibiza Party. Location is #{eventLocation}");

        return variables.stream()
                .map( el -> DynamicTest.dynamicTest(
                        "Replace with variables: " + el,
                        ()->{
                            //Given
                            template.setContent(TEMPLATE_EVENT);
                            client.setVariables(messenger.putVariablesToMap(el));

                            //When
                            templateEngine.replaceVariablesInTemplate(template, client);

                            //Then
                            assertEquals(expectedResult.get(variables.indexOf(el)), template.getContent());
                        }
                ));
    }
}


