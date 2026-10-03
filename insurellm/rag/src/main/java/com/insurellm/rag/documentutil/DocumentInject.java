package com.insurellm.rag.documentutil;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DocumentInject {
    private final VectorStore vectorStore;

    public DocumentInject(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestCompanyDocs() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:knowledge-base/company/*.md");

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(200)
                .build();

        List<Document> chunks = new ArrayList<>();

        for (Resource resource : resources) {
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            Document document = new Document(content);
            document.getMetadata().put("category", "company");
            document.getMetadata().put("source", resource.getFilename());

            chunks.addAll(splitter.split(document));
        }

        vectorStore.add(chunks);
    }

    public void ingestProducts() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:knowledge-base/products/*.md");

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(200)
                .build();

        List<Document> chunks = new ArrayList<>();

        for (Resource resource : resources) {
            String content = new String(
                    resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String product = Objects.requireNonNull(resource.getFilename()).replace(".md", "");
            String[] sections = content.split("(?m)(?=^## )");

            for (String section : sections) {
                String sectionName = section.lines()
                        .findFirst()
                        .orElse("")
                        .replaceFirst("^##\\s*", "")
                        .trim();

                if (sectionName.isEmpty()) {
                    continue;
                }

                Document document = new Document(section);
                document.getMetadata().put("category", "product");
                document.getMetadata().put("product", product);
                document.getMetadata().put("source", resource.getFilename());
                document.getMetadata().put("section", sectionName);

                chunks.addAll(splitter.split(document));
            }
        }

        vectorStore.add(chunks);
    }

    public void ingestEmployees() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:knowledge-base/employees/*.md");

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(200)
                .build();

        List<Document> chunks = new ArrayList<>();

        for (Resource resource : resources) {
            String content = new String(
                    resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String[] sections = content.split("(?m)(?=^## )");

            for (String section : sections) {
                String sectionName = section.lines()
                        .findFirst()
                        .orElse("")
                        .replaceFirst("^##\\s*", "")
                        .trim();

                String name = resource.getFilename();

                Document d = new Document(section);

                d.getMetadata().put("name", name);
                d.getMetadata().put("source", resource.getFilename());
                d.getMetadata().put("section", sectionName);
                d.getMetadata().put("category", "employee");

                chunks.addAll(splitter.split(d));
            }
        }

        vectorStore.add(chunks);
    }
}