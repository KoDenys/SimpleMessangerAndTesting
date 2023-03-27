package com.epam.ld.module2.testing;


import com.epam.ld.module2.testing.template.Template;
import com.epam.ld.module2.testing.template.TemplateEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Messenger.
 */
public class Messenger {
    private MailServer mailServer;
    private TemplateEngine templateEngine;

    /**
     * Instantiates a new Messenger.
     *
     * @param mailServer     the mail server
     * @param templateEngine the template engine
     */
    public Messenger(MailServer mailServer,
                     TemplateEngine templateEngine) {
        this.mailServer = mailServer;
        this.templateEngine = templateEngine;
    }

    /**
     * Send message.
     *
     * @param client   the client
     * @param template the template
     */
    public void sendMessage(Client client, Template template) {
        String messageContent =
            templateEngine.generateMessage(template, client);
        mailServer.send(client, messageContent);
    }

    public void inputDataFromConsole(Template template, Client client){
        Scanner scanner = new Scanner(System.in, StandardCharsets.ISO_8859_1.name());
        String address = scanner.nextLine();
        String name = scanner.nextLine();
        String templateContent = scanner.nextLine();
        String variables = scanner.nextLine();

        client.setAddresses(address);
        client.setName(name);
        client.setVariables(putVariablesToMap(variables));
        template.setContent(templateContent);
    }

    public void inputDataFromFile(Template template, Client client){
        try(BufferedReader reader = new BufferedReader(new FileReader(client.getInputFileName()))){
            client.setAddresses(reader.readLine());
            client.setName(reader.readLine());
            client.setVariables(putVariablesToMap(reader.readLine()));
            template.setContent(reader.readLine());
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public Map<String, String> putVariablesToMap(String text){
        Map<String, String> values = new HashMap<>();
        if(text.length()!=0){
            Pattern pattern = Pattern.compile("#\\{(.*?)\\}=(.*?);");
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String[] vars = matcher.group().split("=");
                values.put(vars[0], vars[1].replace(";", ""));
            }
        }
        return values;
    }
}