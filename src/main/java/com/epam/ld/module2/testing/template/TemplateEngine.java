package com.epam.ld.module2.testing.template;

import com.epam.ld.module2.testing.Client;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The type Template engine.
 */
public class TemplateEngine {
    /**
     * Generate message string.
     *
     * @param template the template
     * @param client   the client
     * @return the string
     */
    public String generateMessage(Template template, Client client) throws InvalidParameterException {
        if(client.getVariables().isEmpty()) {
            throw new NullPointerException("No values were provided at runtime");
        }
        else{
            checkVariablesInTemplate(template, client);
            replaceVariablesInTemplate(template, client);
        }
        StringBuilder message = new StringBuilder();
        message.append(client.getAddresses()).append('\n')
                .append(template.getContent())
                .append("\nBest regards\n")
                .append(client.getName());
        return message.toString();
    }

    public void replaceVariablesInTemplate(Template template, Client client){
        Set<Map.Entry<String, String>> entries = client.getVariables().entrySet();
        Stream<Map.Entry<String, String>> mapStream = entries.stream();
        mapStream.forEach(m -> template.setContent(template.getContent().replace(m.getKey(),m.getValue())));
    }

    public void checkVariablesInTemplate(Template template, Client client) throws InvalidParameterException {
        Pattern pattern = Pattern.compile("#\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(template.getContent());
        while (matcher.find()){
            if(!client.getVariables().containsKey(matcher.group())){
                throw new InvalidParameterException("Variables have redundant value");
            }
        }
    }
}
