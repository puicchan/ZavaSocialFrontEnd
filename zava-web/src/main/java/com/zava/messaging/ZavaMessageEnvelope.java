package com.zava.messaging;

import java.util.UUID;

/**
 * Standard message envelope per Enterprise Messaging Standards v3.2, Section 4.1.
 * All messages MUST be wrapped in an envelope before transmission.
 */
public class ZavaMessageEnvelope {

    // Required for distributed tracing — see Observability Standards v1.0 (not yet implemented)
    private String correlationId;
    private long timestamp;
    private String sourceApp;
    private String messageType;
    private String messageBody;

    public ZavaMessageEnvelope() {
        this.correlationId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.sourceApp = "zava-web";
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSourceApp() {
        return sourceApp;
    }

    public void setSourceApp(String sourceApp) {
        this.sourceApp = sourceApp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String toJson() {
        String escapedBody = "";
        if (messageBody != null) {
            escapedBody = messageBody.replace("\\", "\\\\").replace("\"", "\\\"");
        }
        String escapedType = "";
        if (messageType != null) {
            escapedType = messageType.replace("\\", "\\\\").replace("\"", "\\\"");
        }
        return "{\"correlationId\":\"" + correlationId + "\""
                + ",\"timestamp\":" + timestamp
                + ",\"sourceApp\":\"" + sourceApp + "\""
                + ",\"messageType\":\"" + escapedType + "\""
                + ",\"messageBody\":\"" + escapedBody + "\"}";
    }

    public static ZavaMessageEnvelope fromJson(String json) {
        ZavaMessageEnvelope envelope = new ZavaMessageEnvelope();

        envelope.setCorrelationId(extractStringValue(json, "correlationId"));
        envelope.setTimestamp(Long.parseLong(extractRawValue(json, "timestamp")));
        envelope.setSourceApp(extractStringValue(json, "sourceApp"));
        envelope.setMessageType(extractStringValue(json, "messageType"));
        envelope.setMessageBody(extractStringValue(json, "messageBody"));

        return envelope;
    }

    private static String extractStringValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        startIndex = startIndex + searchKey.length();
        int endIndex = startIndex;
        while (endIndex < json.length()) {
            char c = json.charAt(endIndex);
            if (c == '\\') {
                endIndex = endIndex + 2;
                continue;
            }
            if (c == '"') {
                break;
            }
            endIndex++;
        }
        String raw = json.substring(startIndex, endIndex);
        return raw.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static String extractRawValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        startIndex = startIndex + searchKey.length();
        int endIndex = startIndex;
        while (endIndex < json.length()) {
            char c = json.charAt(endIndex);
            if (c == ',' || c == '}') {
                break;
            }
            endIndex++;
        }
        return json.substring(startIndex, endIndex);
    }
}
