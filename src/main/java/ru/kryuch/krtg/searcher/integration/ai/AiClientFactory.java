// AiClientFactory.java — бин
package ru.kryuch.krtg.searcher.integration.ai;

import org.springframework.stereotype.Component;

@Component
public class AiClientFactory {

    public AiClient createClient(String apiKey) {
        return null;//AiClient.create(apiKey);
    }
}