package com.ragdemo.rag;

import io.netty.util.internal.StringUtil;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Component
public class DocumentLoader {

    private final VectorStore vectorStore;
    private final DocumentChunker documentChunker;

    public DocumentLoader(
            VectorStore vectorStore,
            DocumentChunker documentChunker) {
        this.vectorStore = vectorStore;
        this.documentChunker = documentChunker;
    }

    public void loadDocument() throws IOException {
        ClassPathResource resource =
                new ClassPathResource("datasource/aethergrid.txt");
        String content = new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        log.info("Content: {}", content.substring(0, Math.min(50, content.length())));
        List<Document> chunks = documentChunker.chunkText(content);
        vectorStore.add(chunks);
    }
}