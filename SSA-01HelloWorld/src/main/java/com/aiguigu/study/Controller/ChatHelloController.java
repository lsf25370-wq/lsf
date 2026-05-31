package com.aiguigu.study.Controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
public class ChatHelloController {

    @Resource(name = "ollamaChatModel")
    private ChatModel chatModel;


    @GetMapping("/hello/dochat")
    public String dochat(@RequestParam(name = "msg",defaultValue = "你是谁") String msg) {
        return chatModel.call(msg);
    }

    @GetMapping("/hello/streamchat")
    public Flux<String> streamchat(@RequestParam(name = "msg",defaultValue = "你是谁") String msg) {
        return chatModel.stream(msg);

    }
}
