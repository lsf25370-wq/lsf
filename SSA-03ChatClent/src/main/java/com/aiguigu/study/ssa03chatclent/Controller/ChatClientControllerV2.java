package com.aiguigu.study.ssa03chatclent.Controller;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatClientControllerV2 {

    @Resource
    private ChatModel chatModel;
    
    private final ChatClient chatClient;

    public ChatClientControllerV2(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @GetMapping("/chatmodelv2/dochat")
    public String dochat(@RequestParam(name = "msg",defaultValue = "二加五等于") String msg) {
        return chatModel.call(msg);
    }
    //流式调用
    @GetMapping("/chatmodelv2/streamchat")
    public Flux<String> streamchat(@RequestParam(name = "msg",defaultValue = "二加五等于") String msg)
    {
        return chatModel.stream(msg);
    }
    @GetMapping("/ChatClientv2/dochat")
    public String Clientchat(@RequestParam(name = "msg",defaultValue = "五加六等于") String msg) {
        return chatClient.prompt().user(msg).call().content();
    }
    //流式输出

}
