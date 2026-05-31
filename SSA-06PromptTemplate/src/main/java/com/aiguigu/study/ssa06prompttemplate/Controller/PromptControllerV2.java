package com.aiguigu.study.ssa06prompttemplate.Controller;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
public class PromptControllerV2 {

    @Resource(name = "deepseek")
    private ChatModel deepseekModel;
    @Resource(name = "qwen-plus")
    private ChatModel qwenPlusModel;

    @Resource(name = "deepseekChatClient")
    private ChatClient deepseekchat;
    @Resource(name = "qwenChatClient")
    private ChatClient qwenPluschat;

    @Value("classpath:/prompttemplate/template.txt")
    private org.springframework.core.io.Resource userTemplate;


    @GetMapping("/prompt/chat")
    public Flux<String> chat(@RequestParam String topic, @RequestParam String output_format, @RequestParam String word_count) {
        PromptTemplate promptTemplate = new PromptTemplate("讲一个关于{topic}的故事" + "并以{output_format}的格式输出" + "字数{word_count}左右");

        Prompt prompt = promptTemplate.create(Map.of("topic", topic,
                "output_format", output_format,
                "word_count", word_count));
        return deepseekchat.prompt(prompt).stream().content();
    }

    @GetMapping("/prompt/chat2")
    public Flux<String> chat2(@RequestParam String topic, @RequestParam String output_format, @RequestParam String word_count) {
        PromptTemplate promptTemplate = new PromptTemplate(userTemplate);
        Prompt prompt = promptTemplate.create(Map.of("topic", topic,
                "output_format", output_format,
                "word_count", word_count));
        return deepseekchat.prompt(prompt).stream().content();
    }

    @GetMapping("/prompt/chat3")
    public String chat3(String sysTopic, String userTopic) {
        //1系统提示

        //3组合
        //4调用模型
        //5返回结果
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("你是一个{sysTopic}的助手，只回答{sysTopic}问题");
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("sysTopic", sysTopic));

//2用户提示
        PromptTemplate userPromptTemplate = new PromptTemplate("解释{userTopic}");
        Message userMessage = userPromptTemplate.createMessage(Map.of("userTopic", userTopic));

        //3组合
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        //4调用
        return deepseekchat.prompt(prompt).call().content();
    }

    //使用chatmodel调用模型
    @GetMapping("/prompt/chat4")
    public String chat4(String question){
        //1系统提示
        SystemMessage systemMessage = new SystemMessage("你是一个专业的助手");
        //2用户提示
        UserMessage userMessage = new UserMessage(question);
        //3组合
        Prompt prompt = new Prompt(List.of(systemMessage,userMessage));
        //4调用
        return deepseekModel.call(prompt).getResult().getOutput().getText();

    }

    //使用chatmodel调用模型
    @GetMapping("/prompt/chat5")
    public Flux<String> chat5(String question){
        return deepseekchat.prompt()
                .system("你是一个专业的java助手")
                .user(question)
                .stream()
                .content();

    }
}