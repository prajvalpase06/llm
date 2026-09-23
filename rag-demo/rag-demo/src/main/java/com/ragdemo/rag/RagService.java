package com.ragdemo.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
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
//        String information = similarDocuments.stream().map(Document::getText).collect(Collectors.joining("\n\n--- CHUNK ---\n\n"));

        for (Document document : similarDocuments) {
            log.info("----- DOCUMENT -----");
            log.info("Text: {}", document.getText());
            log.info("Metadata: {}", document.getMetadata());
        }

        return similarDocuments.getFirst().getText();
    }


}
