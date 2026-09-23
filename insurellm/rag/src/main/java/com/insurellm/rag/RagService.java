package com.insurellm.rag;

import com.insurellm.rag.documentutil.DocumentInject;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagService {
    private final PgVectorStore vectorStore;
    private final DocumentInject documentInject;
    private final ChatClient chatClient;
    private final SystemPromptTemplate systemPromptTemplate;

    public RagService(DocumentInject documentInject,PgVectorStore vectorStore,ChatClient.Builder chatClientBuilder,@Value("classpath:/prompts/prompt.st") Resource ragPromptTemplate) {
        this.documentInject=documentInject;
        this.vectorStore=vectorStore;
        this.chatClient=chatClientBuilder.build();
        this.systemPromptTemplate=new SystemPromptTemplate(ragPromptTemplate);
    }

    public String injectCompanyRecords() throws IOException {
        documentInject.ingestCompanyDocs();
        return "Inject Company details";
    }

    public String retreiveRelevantContext(String query) {
        SearchRequest request=SearchRequest.builder().query(query).topK(2).build();
        List<Document> relevant=vectorStore.doSimilaritySearch(request);
        return relevant.stream().map(Document::getText).collect(Collectors.joining("\n\n---CHUNK---\n\n"));
    }

    public Flux<String> streamResponse(String question) {
        String context=retreiveRelevantContext(question);
        Prompt prompt=new Prompt(List.of(systemPromptTemplate.createMessage(Map.of("question",question,"context",context))));
        return chatClient.prompt(prompt).stream().content();
    }
}