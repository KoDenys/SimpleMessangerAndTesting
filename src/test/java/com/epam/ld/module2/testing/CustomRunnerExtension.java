package com.epam.ld.module2.testing;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class CustomRunnerExtension implements AfterTestExecutionCallback {

        @Override
        public void afterTestExecution(ExtensionContext context) throws Exception {
            StringBuilder infoBuilder = new StringBuilder();
            List<String> previousInfo = Files.readAllLines(Paths.get("src/test/resources/testResults.txt"));
            for(String line : previousInfo){
                infoBuilder.append(line)
                        .append("\n");
            }
            boolean testResult = context.getExecutionException().isPresent();
            infoBuilder.append(LocalDateTime.now())
                    .append(" ")
                    .append(context.getDisplayName())
                    .append(" was failed: ")
                    .append(testResult);
            Files.write(Paths.get("src/test/resources/testResults.txt"), infoBuilder.toString().getBytes(StandardCharsets.ISO_8859_1));
        }
    }
