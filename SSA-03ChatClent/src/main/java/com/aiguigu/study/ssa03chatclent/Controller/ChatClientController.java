package com.aiguigu.study.ssa03chatclent.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ChatClientController {


    private final ChatClient dashScopeChatClient;


    // 构造函数注入
    public ChatClientController(ChatModel dashScopeChatClient) {
        this.dashScopeChatClient = ChatClient
                .builder(dashScopeChatClient).build();

    }
    @GetMapping("/chatmodel/dochat")
    public String dochat(@RequestParam(name = "msg",defaultValue = "一加一等于多少")String msg){
        return dashScopeChatClient.prompt().user(msg).call().content();
    }
}
