package com.ragdemo.rag;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ragdemo.rag.RagService.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> ingest() throws IOException {
        return ragService.load();
    }

    @PostMapping("/retreive")
    public String retrieve(@RequestBody String query){
        return ragService.retrieveAndGenerateAnswer(query);
    }
}
