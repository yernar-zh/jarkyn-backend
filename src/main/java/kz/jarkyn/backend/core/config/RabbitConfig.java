package kz.jarkyn.backend.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.queue.document-search}")
    private String supplyIndexQueueName;

    @Value("${rabbitmq.queue.audit-save:audit-save}")
    private String auditSaveQueueName;

    @Bean
    public Queue supplyIndexQueue() {
        return new Queue(supplyIndexQueueName, true);
    }

    @Bean
    public Queue auditSaveQueue() {
        return new Queue(auditSaveQueueName, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("main-exchange");
    }

    @Bean
    public Binding binding(Queue supplyIndexQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(supplyIndexQueue)
                .to(exchange)
                .with(RabbitRoutingKeys.DOCUMENT_SEARCH);
    }

    @Bean
    public Binding auditSaveBinding(Queue auditSaveQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(auditSaveQueue)
                .to(exchange)
                .with(RabbitRoutingKeys.AUDIT_SAVE);
    }

    @Bean
    public MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setExchange("main-exchange");
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}