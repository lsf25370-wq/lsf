package com.aiguigu.study.ssa04streamingoutput.Controller;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
public class StreamOutputControllerV2 {

    @Resource(name = "deepseek")
    private ChatModel deepseekModel;
    @Resource(name = "qwen-plus")
    private ChatModel qwenPlusModel;
    
    @Resource(name = "deepseekChatClient")
    private ChatClient deepseekchat;

    @Resource(name = "qwenChatClient")
    private ChatClient qwenPluschat;


    @GetMapping("/stream/dchatflux1")
    public Flux<String> dochat(@RequestParam(name = "msg",defaultValue = "二加五等于") String msg) {

        return deepseekModel.stream(msg);
    }
    //流式调用
    @GetMapping("/stream/qchatflux1")
    public Flux<String> streamchat(@RequestParam(name = "msg",defaultValue = "二加五等于") String msg)
    {
        return qwenPlusModel.stream(msg);
    }

//// 使用的模型依然是 deepseek-v4-flash
//通过chatClient实现流式输出
    @GetMapping("/ChatClientv2/dstreamchat")
    public Flux<String> dstreamchat(@RequestParam(name = "msg",defaultValue = "五加六等于") String msg) {
        return deepseekchat.prompt().user(msg).stream().content();
    }

    //qwen-plus模型的流式输出
    @GetMapping("/ChatClientv2/qstreamchat")
    public Flux<String> qstreamchat(@RequestParam(name = "msg",defaultValue = "五加六等于") String msg) {
        return qwenPluschat.prompt().user(msg).stream().content();
    }

    // 支持模型选择的非流式问答接口
    @PostMapping("/api/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String model = request.get("model");
        
        String answer;
        if ("qwen-plus".equals(model)) {
            answer = qwenPlusModel.call(question);
        } else {
            answer = deepseekModel.call(question);
        }
        
        return Map.of("answer", answer);
    }
}