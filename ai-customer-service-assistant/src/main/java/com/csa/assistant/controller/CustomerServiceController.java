package com.csa.assistant.controller;

import com.csa.assistant.model.CustomerQuery;
import com.csa.assistant.model.ServiceResponse;
import com.csa.assistant.service.IntelligentAgentService;
import com.csa.assistant.service.KnowledgeBaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer-service")
public class CustomerServiceController {

    private final IntelligentAgentService agentService;
    private final KnowledgeBaseService knowledgeBaseService;

    public CustomerServiceController(IntelligentAgentService agentService,
                                     KnowledgeBaseService knowledgeBaseService) {
        this.agentService = agentService;
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @GetMapping("/chat")
    public ServiceResponse chat(
            @RequestParam(name = "msg") String message,
            @RequestParam(name = "sessionId", required = false) String sessionId,
            @RequestParam(name = "userId", required = false, defaultValue = "anonymous") String userId) {

        String sid = (sessionId != null && !sessionId.isBlank()) ? sessionId : UUID.randomUUID().toString();

        CustomerQuery query = new CustomerQuery(sid, message, userId);
        return agentService.processQuery(query);
    }

    @GetMapping("/stream-chat")
    public Flux<String> streamChat(
            @RequestParam(name = "msg") String message,
            @RequestParam(name = "sessionId", required = false) String sessionId,
            @RequestParam(name = "userId", required = false, defaultValue = "anonymous") String userId) {

        String sid = (sessionId != null && !sessionId.isBlank()) ? sessionId : UUID.randomUUID().toString();

        CustomerQuery query = new CustomerQuery(sid, message, userId);
        return agentService.processQueryStream(query);
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "AI Customer Service Assistant",
                "knowledge_base_size", knowledgeBaseService.getFAQCount(),
                "timestamp", System.currentTimeMillis()
        );
    }
}
