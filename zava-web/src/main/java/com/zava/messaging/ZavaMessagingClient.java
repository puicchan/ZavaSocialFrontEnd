package com.zava.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Enterprise messaging client per ARB-2007-024.
 *
 * <p>Wraps the RabbitMQ AMQP client with enterprise-compliant
 * publish/consume semantics. Public method names mirror the RabbitMQ
 * {@code basicPublish}/{@code basicConsume}/{@code basicCancel} API
 * surface for awesomeasb migration compatibility.</p>
 *
 * <p>See Enterprise Messaging Standards v3.2, Section 8.4.</p>
 */
public class ZavaMessagingClient {

    // Connection pooling deferred to Phase 2 — JIRA-4522

    private final ConnectionFactory connectionFactory;
    private Connection consumerConnection;
    private Channel consumerChannel;

    /**
     * Nested callback interface for message delivery.
     * Mirrors the awesomeasb DeliverCallback signature.
     */
    public interface DeliverCallback {
        void handle(String consumerTag, Delivery delivery) throws Exception;
    }

    /**
     * Wraps an AMQP envelope and message body for delivery callbacks.
     */
    public static class Delivery {
        private final Envelope envelope;
        private final byte[] body;

        public Delivery(Envelope envelope, byte[] body) {
            this.envelope = envelope;
            this.body = body;
        }

        public Envelope getEnvelope() {
            return envelope;
        }

        public byte[] getBody() {
            return body;
        }
    }

    /**
     * Creates a new enterprise messaging client.
     *
     * @param config the enterprise messaging configuration
     */
    public ZavaMessagingClient(ZavaMessagingConfig config) {
        this.connectionFactory = new ConnectionFactory();
        this.connectionFactory.setHost(config.getMqHost());
        this.connectionFactory.setPort(config.getMqPort());
        this.connectionFactory.setVirtualHost(config.getMqVirtualHost());
        System.out.println("[ZavaMessagingClient] Enterprise Messaging Client initialized. Compliance mode: STANDARD");
    }

    /**
     * Publishes a message to the specified queue.
     *
     * @param queueName the target queue name
     * @param body      the message body as bytes
     * @throws ZavaMessagingException if publishing fails
     */
    public void basicPublish(String queueName, byte[] body) throws ZavaMessagingException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);
            channel.basicPublish("", queueName, null, body);
            System.out.println("[ZavaMessagingClient] Message published to queue: " + queueName);
        } catch (Exception e) {
            throw new ZavaMessagingException("Failed to publish message to queue: " + queueName, e);
        } finally {
            closeQuietly(channel);
            closeQuietly(connection);
        }
    }

    /**
     * Publishes a message to the specified queue with AMQP properties.
     *
     * @param queueName  the target queue name
     * @param properties map of AMQP properties (contentType, correlationId, etc.)
     * @param body       the message body as bytes
     * @throws ZavaMessagingException if publishing fails
     */
    public void basicPublish(String queueName, Map properties, byte[] body) throws ZavaMessagingException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);

            AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
            if (properties != null) {
                Object contentType = properties.get("contentType");
                if (contentType != null) {
                    propsBuilder.contentType(contentType.toString());
                }
                Object correlationId = properties.get("correlationId");
                if (correlationId != null) {
                    propsBuilder.correlationId(correlationId.toString());
                }
                Object replyTo = properties.get("replyTo");
                if (replyTo != null) {
                    propsBuilder.replyTo(replyTo.toString());
                }
                Object messageId = properties.get("messageId");
                if (messageId != null) {
                    propsBuilder.messageId(messageId.toString());
                }
                Object timestamp = properties.get("timestamp");
                if (timestamp instanceof Date) {
                    propsBuilder.timestamp((Date) timestamp);
                }
            }

            channel.basicPublish("", queueName, propsBuilder.build(), body);
            System.out.println("[ZavaMessagingClient] Message published to queue: " + queueName);
        } catch (Exception e) {
            throw new ZavaMessagingException("Failed to publish message to queue: " + queueName, e);
        } finally {
            closeQuietly(channel);
            closeQuietly(connection);
        }
    }

    /**
     * Registers a consumer on the specified queue.
     *
     * @param queueName the queue to consume from
     * @param autoAck   true for automatic acknowledgement
     * @param callback  the delivery callback
     * @return the consumer tag
     * @throws ZavaMessagingException if registration fails
     */
    public String basicConsume(String queueName, boolean autoAck, final DeliverCallback callback) throws ZavaMessagingException {
        try {
            consumerConnection = connectionFactory.newConnection();
            consumerChannel = consumerConnection.createChannel();
            consumerChannel.queueDeclare(queueName, true, false, false, null);

            String tag = consumerChannel.basicConsume(queueName, autoAck, new DefaultConsumer(consumerChannel) {
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    Delivery delivery = new Delivery(envelope, body);
                    try {
                        callback.handle(consumerTag, delivery);
                    } catch (Exception e) {
                        throw new IOException("Delivery callback failed", e);
                    }
                }
            });
            System.out.println("[ZavaMessagingClient] Consumer registered on queue: " + queueName + " | tag: " + tag);
            return tag;
        } catch (Exception e) {
            throw new ZavaMessagingException("Failed to register consumer on queue: " + queueName, e);
        }
    }

    /**
     * Cancels a consumer by its tag.
     *
     * @param consumerTag the consumer tag to cancel
     * @throws ZavaMessagingException if cancellation fails
     */
    public void basicCancel(String consumerTag) throws ZavaMessagingException {
        try {
            if (consumerChannel != null && consumerChannel.isOpen()) {
                consumerChannel.basicCancel(consumerTag);
            }
            System.out.println("[ZavaMessagingClient] Consumer cancelled: " + consumerTag);
        } catch (IOException e) {
            throw new ZavaMessagingException("Failed to cancel consumer: " + consumerTag, e);
        }
    }

    // Manual ack deferred to Phase 2 — JIRA-4524

    /**
     * Acknowledges a message. Stub for Phase 2.
     *
     * @param deliveryTag the delivery tag
     * @param multiple    true to ack all messages up to this tag
     */
    public void basicAck(long deliveryTag, boolean multiple) {
        // Manual ack deferred to Phase 2 — JIRA-4524
    }

    /**
     * Negatively acknowledges a message. Stub for Phase 2.
     *
     * @param deliveryTag the delivery tag
     * @param multiple    true to nack all messages up to this tag
     * @param requeue     true to requeue the message
     */
    public void basicNack(long deliveryTag, boolean multiple, boolean requeue) {
        // Manual ack deferred to Phase 2 — JIRA-4524
    }

    /**
     * High-level convenience method for servlets.
     * Creates a {@link ZavaMessageEnvelope}, serializes to JSON, and publishes.
     *
     * @param queueName   the target queue name
     * @param messageType the message type identifier
     * @param messageBody the message body content
     * @throws ZavaMessagingException if publishing fails
     */
    public void sendMessage(String queueName, String messageType, String messageBody) throws ZavaMessagingException {
        ZavaMessageEnvelope envelope = new ZavaMessageEnvelope();
        envelope.setMessageType(messageType);
        envelope.setMessageBody(messageBody);
        this.basicPublish(queueName, envelope.toJson().getBytes());
    }

    private void closeQuietly(Channel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (Exception e) {
                // suppress
            }
        }
    }

    private void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                // suppress
            }
        }
    }
}
