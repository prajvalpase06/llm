package com.insurellm.rag;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("insurellm/rag")
public class RagController {
    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/inject/company")
    public String injectCompany() throws IOException {

        return ragService.injectCompanyRecords();
    }

    @PostMapping("/ask")
    public String retreiveRelevant(@RequestBody String query) throws IOException {
        return ragService.respondWithContext(query);
    }


}
