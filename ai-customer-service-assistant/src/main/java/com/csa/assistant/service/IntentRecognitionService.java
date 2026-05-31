package com.csa.assistant.service;

import com.csa.assistant.model.IntentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class IntentRecognitionService {

    private final ChatClient chatClient;

    public IntentRecognitionService(ChatClient customerServiceChatClient) {
        this.chatClient = customerServiceChatClient;
    }

    public IntentType recognizeIntent(String userQuery) {
        String intentPrompt = buildIntentPrompt(userQuery);
        String response = chatClient.prompt()
                .user(intentPrompt)
                .call()
                .content();

        return parseIntentResponse(response);
    }

    private String buildIntentPrompt(String userQuery) {
        return """
                你是一个意图识别专家。请分析用户输入，判断其意图类型。
                
                意图类型定义：
                - product_inquiry：产品咨询（询问商品信息、价格、规格、功能等）
                - order_issue：订单问题（发货、物流、退款、修改订单等）
                - technical_support：技术支持（账号问题、系统故障、使用方法等）
                - complaint：投诉建议（对服务或产品不满、提出改进意见等）
                - general_faq：常见问题（一般性咨询，不涉及具体业务的询问）
                - escalation：明确要求转接人工客服
                
                用户输入：%s
                
                请只回复意图代码，不要有任何其他内容。例如：order_issue
                """.formatted(userQuery);
    }

    private IntentType parseIntentResponse(String response) {
        if (response == null || response.isBlank()) {
            return IntentType.GENERAL_FAQ;
        }

        String cleanResponse = response.trim().toLowerCase();

        if (cleanResponse.contains("product_inquiry") || cleanResponse.contains("产品咨询")) {
            return IntentType.PRODUCT_INQUIRY;
        }
        if (cleanResponse.contains("order_issue") || cleanResponse.contains("订单")) {
            return IntentType.ORDER_ISSUE;
        }
        if (cleanResponse.contains("technical_support") || cleanResponse.contains("技术")) {
            return IntentType.TECHNICAL_SUPPORT;
        }
        if (cleanResponse.contains("complaint") || cleanResponse.contains("投诉")) {
            return IntentType.COMPLAINT;
        }
        if (cleanResponse.contains("escalation") || cleanResponse.contains("人工") || cleanResponse.contains("转接")) {
            return IntentType.ESCALATION;
        }
        if (cleanResponse.contains("general_faq") || cleanResponse.contains("一般")) {
            return IntentType.GENERAL_FAQ;
        }

        return IntentType.GENERAL_FAQ;
    }
}
