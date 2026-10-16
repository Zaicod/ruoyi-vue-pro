package cn.iocoder.yudao.module.study.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "study.ai", name = "enabled", havingValue = "true")
public class StudyAiConfiguration {

    @Bean
    public ChatClient studyChatClient(
            @Value("${study.ai.api-key:}") String apiKey,
            @Value("${study.ai.base-url:https://api.openai.com}") String baseUrl,
            @Value("${study.ai.model:}") String model) {
        Assert.hasText(apiKey, "启用学习助手时必须配置 study.ai.api-key");
        Assert.hasText(model, "启用学习助手时必须配置 study.ai.model");
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder().baseUrl(baseUrl).apiKey(apiKey).build())
                .defaultOptions(OpenAiChatOptions.builder().model(model).build())
                .build();
        return ChatClient.builder(chatModel).build();
    }
}
