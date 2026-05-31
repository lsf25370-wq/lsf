package com.csa.assistant.model;

public enum IntentType {

    PRODUCT_INQUIRY("product_inquiry", "产品咨询"),
    ORDER_ISSUE("order_issue", "订单问题"),
    TECHNICAL_SUPPORT("technical_support", "技术支持"),
    COMPLAINT("complaint", "投诉建议"),
    ACCOUNT_HELP("account_help", "账户相关"),
    GENERAL_FAQ("general_faq", "常见问题"),
    ESCALATION("escalation", "人工转接"),
    UNKNOWN("unknown", "未识别");

    private final String code;
    private final String description;

    IntentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static IntentType fromCode(String code) {
        for (IntentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
