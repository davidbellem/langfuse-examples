package com.langfuse.springai;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;

    private final Tracer tracer;

    public ChatService(ChatClient.Builder builder, Tracer tracer) {
        chatClient = builder.build();
        this.tracer = tracer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public String testAiCall() {
        // Create a new span with user and session attributes
        Span span = tracer.spanBuilder("ai_interaction")
                .setAttribute("langfuse.user.id", "123")
                .setAttribute("langfuse.session.id", "123")
                .startSpan();

        // Make sure to set the new span as active.
        try (Scope ignored = span.makeCurrent()) {
            // Your AI processing logic here
            // Any spans created within this scope will be children of the parent span
            String result = performAiOperation(span);
            return result;
        } finally {
            span.end();
        }
    }

    private String performAiOperation(Span span) {
        LOGGER.info("Invoking LLM");
        String prompt = "Reply with the word 'java'";
        span.setAttribute("langfuse.trace.input", prompt);
        String answer = chatClient.prompt(prompt).call().content();
        LOGGER.info("AI answered: {}", answer);
        span.setAttribute("langfuse.trace.output", answer);
        return answer;
    }
}
