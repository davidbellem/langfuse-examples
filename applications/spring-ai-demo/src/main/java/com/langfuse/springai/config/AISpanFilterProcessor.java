package com.langfuse.springai.config;

import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom OpenTelemetry SpanProcessor that filters spans to only export AI-relevant traces to Langfuse.
 * This helps reduce noise and costs by only sending spans related to AI/LLM operations.
 */
public class AISpanFilterProcessor implements SpanProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AISpanFilterProcessor.class);

    private final SpanProcessor delegate;

    public AISpanFilterProcessor(SpanProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
        if (isAIRelevantSpan(span)) {
            logger.trace("Starting AI-relevant span: {}", span.getName());
            delegate.onStart(parentContext, span);
        } else {
            logger.trace("Filtering out non-AI span: {}", span.getName());
        }
    }

    @Override
    public void onEnd(ReadableSpan span) {
        if (isAIRelevantSpan(span)) {
            logger.trace("Ending AI-relevant span: {}", span.getName());
            delegate.onEnd(span);
        }
    }

    /**
     * Determines if a span is relevant for AI/LLM observability.
     *
     * @param span The span to evaluate
     * @return true if the span should be exported to Langfuse
     */
    private boolean isAIRelevantSpan(ReadableSpan span) {
        String spanName = span.getName();

        // Filter by span name patterns - AI/LLM related operations
        if (spanName.startsWith("spring_ai") || spanName.startsWith("ai_")) {
            return true;
        }

        // Filter by OpenTelemetry semantic conventions for AI
        return span.getAttributes().asMap().entrySet().stream()
                .anyMatch(entry -> {
                    String key = entry.getKey().getKey();
                    return key.startsWith("gen_ai") || key.startsWith("langfuse");
                });
    }

    @Override
    public boolean isStartRequired() {
        return delegate.isStartRequired();
    }

    @Override
    public boolean isEndRequired() {
        return delegate.isEndRequired();
    }

    @Override
    public void close() {
        delegate.close();
    }
}