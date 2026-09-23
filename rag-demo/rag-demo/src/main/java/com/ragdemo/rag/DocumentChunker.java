package com.ragdemo.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentChunker {

    public List<Document> chunkText(String doc) {

        Document document = new Document(doc);

        TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(100)
                .withMinChunkSizeChars(50)
                .withMinChunkLengthToEmbed(20)
                .withMaxNumChunks(200)
                .withKeepSeparator(true)
                .build();

        return textSplitter.apply(List.of(document));
    }
}