package com.ragdemo.rag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RagService {
    private final DocumentLoader documentLoader;

    public RagService(DocumentLoader documentLoader){
        this.documentLoader = documentLoader;
    }

    public ResponseEntity<String> load() throws IOException {
        String responseMessage = "Document loaded successfully";
        documentLoader.loadDocument();
        return ResponseEntity.ok().body(responseMessage);
    }
}
