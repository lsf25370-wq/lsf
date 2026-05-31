package com.aiguigu.study.ssa05prompt.Controller;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

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


    @GetMapping("prompt/chat")
    public Flux<String> chat(String msg){
        return  deepseekchat.prompt()
                .system("你是一个法律助手，只回答法律问题，其他问题一概回复，我是能回答法律相关问题其他无可奉告")
                .user(msg)
                .stream()
                .content();
    }

    //使用chatmodel写一个chat2
    @GetMapping("prompt/chat2")
    public Flux<ChatResponse> chat2(@RequestParam(name = "msg", defaultValue = "讲个故事")String msg){
        //系统消息
        SystemMessage systemMessage = new SystemMessage( "你是一个讲故事的助手，每个故事控制在300字以内，其他无可奉告");
       //用户消息
        UserMessage userMessage =new UserMessage(msg);

        Prompt prompt= new Prompt(systemMessage,userMessage);
        return deepseekModel.stream(prompt);

    }
    @GetMapping("prompt/chat3")
    public Flux<String> chat3(@RequestParam(name = "msg", defaultValue = "讲个故事")String msg){
        //系统消息
        SystemMessage systemMessage = new SystemMessage( "你是一个讲故事的助手，每个故事控制在300字以内，其他无可奉告");
        //用户消息
        UserMessage userMessage =new UserMessage(msg);

        Prompt prompt= new Prompt(systemMessage,userMessage);
        return deepseekModel.stream(prompt)
                .map(response -> response.getResults().get(0).getOutput().getText());

    }

    //使用AssistantMessage写一个chat4
    @GetMapping("prompt/chat4")
    public String chat4(@RequestParam(name = "msg", defaultValue = "讲个故事")String msg){

        AssistantMessage assistantMessage =deepseekchat.prompt()
                .user(msg)
                .call()
                .chatResponse()
                .getResult()
                .getOutput();
        return assistantMessage.getText();
    }

    //尝试tool调用
    @GetMapping("prompt/chat5")
    public String chat5(String city){

        String answer =deepseekchat.prompt()
                .user("查询"+city+"未来三天的天气")
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        ToolResponseMessage toolResponseMessage =
                new ToolResponseMessage(
                        List.of(new ToolResponseMessage.ToolResponse("1","获得天气信息",answer))
                                       );
        String toolResponseMessageStr=toolResponseMessage.getText();


        return answer+toolResponseMessageStr;
    }
}
