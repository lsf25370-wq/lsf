package com.aiguigu.study.ssa08persistent.Controller;


import com.aiguigu.study.ssa07structuredoutput.records.StudentRecord;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

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



    @GetMapping("structuredOutput/chat")
    public StudentRecord chat
            (@RequestParam(name="sname") String sname,
             @RequestParam(name="email") String email){
        return qwenPluschat.prompt().user(new Consumer<ChatClient.PromptUserSpec>() {
            @Override
            public void accept(ChatClient.PromptUserSpec promptUserSpec) {
                promptUserSpec.text("学号1001，我叫{sname},大学专业计算机，邮箱{email}")
                        .param("sname",sname)
                        .param("email",email);
            }
        }).call().entity(StudentRecord.class);
        }
    @GetMapping("structuredOutput/chat2")
    public StudentRecord chat2
            (@RequestParam(name="sname") String sname,
             @RequestParam(name="email") String email){
        return qwenPluschat.prompt()
                .user(userSpec -> userSpec
                        .text("学号1001，我叫{sname},大学专业计算机，邮箱{email}")
                        .param("sname", sname)
                        .param("email", email))
                .call()
                .entity(StudentRecord.class);
    }

}