package com.ragdemo.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.prompt.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
public class RagService {
    private final DocumentLoader documentLoader;
    private final ChatClient chatClient;
    private final PgVectorStore vectorStore;

    @Value("classpath:/prompts/rag-prompt.st")
    private Resource ragPromptTemplate;

    public RagService(DocumentLoader documentLoader,
                      ChatClient.Builder chatClientBuilder, PgVectorStore vectorStore){
        this.documentLoader = documentLoader;
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public ResponseEntity<String> load() throws IOException {
        String responseMessage = "Document loaded successfully";
        documentLoader.loadDocument();
        return ResponseEntity.ok().body(responseMessage);
    }

    public String retrieveAndGenerateAnswer(String message) {
        // 1. Retrieve relevant documents
        SearchRequest searchRequest = SearchRequest.builder().query(message).topK(4).build();
        List<Document> similarDocuments = vectorStore.doSimilaritySearch(searchRequest);

        // 4. Combine retrieved chunks into context
        String context = similarDocuments.stream()
                        .map(Document::getText)
                        .collect(Collectors.joining("\n\n---CHUNK----\n\n"));

        // 5. Send question + retrieved context to the LLM
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(ragPromptTemplate);
        Prompt prompt = new Prompt(List.of(systemPromptTemplate.createMessage(Map.of("question", message,
                                                                                     "context", context))));
//        return chatClient.prompt(prompt).call().content();

        var response = chatClient.prompt(prompt)

                .call();

        var chatResponse = response.chatResponse();

        System.out.println("CHAT RESPONSE = " + chatResponse);

        if (chatResponse == null) {

            return "ChatResponse is null";

        }

        System.out.println("RESULT = " + chatResponse.getResult());

        if (chatResponse.getResult() == null) {

            return "ChatResponse result is null";

        }

        System.out.println("OUTPUT = " + chatResponse.getResult().getOutput());

        if (chatResponse.getResult().getOutput() == null) {

            return "ChatResponse output is null";

        }

        System.out.println("TEXT = " +

                chatResponse.getResult().getOutput().getText());

        return chatResponse.getResult().getOutput().getText();
    }
}
