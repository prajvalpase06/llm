package com.insurellm.rag;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import java.io.IOException;

@RestController
@RequestMapping("insurellm/rag")
public class RagController {
    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService=ragService;
    }

    @PostMapping("/inject/company")
    public String injectCompany() throws IOException {
        return ragService.injectCompanyRecords();
    }

    @PostMapping("/inject/products")
    public String injectProducts() throws IOException {
        return ragService.injectProducts();
    }

    @PostMapping(value="/ask",produces=MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> ask(@RequestBody String query) {
        return ragService.streamResponse(query);
    }

    @PostMapping("/inject/employees")
    public String injectEmployees() throws IOException {
        return ragService.injectEmployees();
    }
}