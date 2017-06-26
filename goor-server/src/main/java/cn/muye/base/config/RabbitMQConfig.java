package cn.muye.base.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Selim on 2017/6/26.
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue directMessageCommand() {
        return new Queue("direct.command");
    }

    @Bean
    public Queue directMessageResource() {
        return new Queue("direct.resource");
    }

    @Bean
    public Queue directMessageCommon() {
        return new Queue("direct.common");
    }

    @Bean
    public Queue fanoutMessageCommand() {
        return new Queue("fanout.command");
    }

    @Bean
    public Queue fanoutMessageResource() {
        return new Queue("fanout.resource");
    }

    @Bean
    public Queue topicMessageCommand() {
        return new Queue("topic.command");
    }

    @Bean
    public Queue topicMessages() {
        return new Queue("topic.messages");
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topicExchange");
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanoutExchange");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("directExchange");
    }

    @Bean
    public Binding bindingTopicExchangeMessage(Queue topicMessageCommand, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicMessageCommand).to(topicExchange).with("topic.message");
    }

    @Bean
    public Binding bindingTopicExchangeMessages(Queue topicMessages, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicMessages).to(topicExchange).with("topic.#");
    }

    @Bean
    public Binding bindingDirectExchangeCommand(Queue directMessageCommand, DirectExchange directExchange) {
        return BindingBuilder.bind(directMessageCommand).to(directExchange).with("direct.common");
    }

    @Bean
    public Binding bindingDirectExchangeResource(Queue directMessageResource, DirectExchange directExchange) {
        return BindingBuilder.bind(directMessageResource).to(directExchange).with("direct.common");
    }

    @Bean
    public Binding bindingFanoutExchangeCommand(Queue fanoutMessageCommand, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutMessageCommand).to(fanoutExchange);
    }

    @Bean
    public Binding bindingFanoutExchangeResource(Queue fanoutMessageResource, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutMessageResource).to(fanoutExchange);
    }

}
