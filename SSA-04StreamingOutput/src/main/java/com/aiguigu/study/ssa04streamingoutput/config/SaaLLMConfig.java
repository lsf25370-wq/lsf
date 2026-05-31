package com.aiguigu.study.ssa04streamingoutput.config;


import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaaLLMConfig {

//    @Value("${spring.ai.dashscope.api-key}")
//    private String apiKey;
//
//    @Bean
//    public DashScopeApi dashScopeApi() {
//        return DashScopeApi.builder()
//                .apiKey(apiKey)
//                .build();
//    }

    private final String  DEEPSEEK_MODEL = "deepseek-v4-flash";
    private final String  QWEN_MODEL = "qwen-plus";

    @Bean(name = "deepseek")
    public ChatModel deepseek() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(System.getenv("aliQwen-api")).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(DEEPSEEK_MODEL).build())
                .build();
    }
    @Bean(name = "qwen-plus")
    public ChatModel qwenPlus() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(System.getenv("aliQwen-api")).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(QWEN_MODEL).build())
                .build();
    }

    //使用chatClient
    @Bean(name = "deepseekChatClient")
    public ChatClient deepseekChatClient(@Qualifier("deepseek") ChatModel deepseek) {
        return ChatClient.builder(deepseek).build();
    }

    @Bean(name = "qwenChatClient")
    public ChatClient qwenChatClient(@Qualifier("qwen-plus") ChatModel qwenPlus) {
        return ChatClient.builder(qwenPlus)
                .defaultOptions(DashScopeChatOptions.builder().withModel(QWEN_MODEL).build())
                .build();
    }

}