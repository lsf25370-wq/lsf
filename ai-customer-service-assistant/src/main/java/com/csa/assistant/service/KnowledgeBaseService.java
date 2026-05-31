package com.csa.assistant.service;

import com.csa.assistant.model.FAQEntry;
import com.csa.assistant.model.IntentType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseService {

    private final List<FAQEntry> faqDatabase = new ArrayList<>();

    @PostConstruct
    public void init() {
        addFAQ("如何重置密码", "您可以通过官网登录页面点击'忘记密码'，按照邮件提示重置密码。也可拨打客服热线 400-xxx-xxxx 进行人工重置。", "账户管理", IntentType.TECHNICAL_SUPPORT);
        addFAQ("退货流程是怎样的", "退货流程：1) 在订单页面申请退货 2) 选择退货原因 3) 等待审核（1-2个工作日）4) 审核通过后寄回商品 5) 仓库签收后 3-5 个工作日退款到账。", "售后服务", IntentType.ORDER_ISSUE);
        addFAQ("商品什么时候发货", "正常情况下，订单支付成功后 24 小时内发货。定制商品需要 3-7 个工作日。如遇大促活动，发货时间可能延长 1-3 天。", "物流配送", IntentType.ORDER_ISSUE);
        addFAQ("如何查询物流信息", "您可以在订单详情页查看实时物流信息。也可登录快递公司官网输入快递单号查询。", "物流配送", IntentType.ORDER_ISSUE);
        addFAQ("如何修改收货地址", "未发货订单：可在订单详情页直接修改地址。已发货订单：请联系客服 400-xxx-xxxx 协助拦截并修改。", "订单管理", IntentType.ORDER_ISSUE);
        addFAQ("如何取消订单", "未发货订单可在订单详情页直接取消。已发货订单需先拒收，或联系客服申请退货退款。", "订单管理", IntentType.ORDER_ISSUE);
        addFAQ("支付方式有哪些", "我们支持支付宝、微信支付、银联卡、花呗分期和信用卡支付。部分商品支持货到付款。", "支付相关", IntentType.GENERAL_FAQ);
        addFAQ("开发票需要多长时间", "电子发票在订单完成后 24 小时内自动开具并发送到您的邮箱。纸质发票需要 3-5 个工作日寄出。", "售后服务", IntentType.GENERAL_FAQ);
        addFAQ("商品有质量问题怎么办", "收到商品后 7 天内发现质量问题可申请退换货。请拍照留证并通过订单页面提交退换货申请，客服会在 24 小时内处理。", "售后服务", IntentType.COMPLAINT);
        addFAQ("优惠券如何使用", "在结算页面选择'使用优惠券'，系统会自动匹配可用优惠券。注意：优惠券有使用期限和最低消费限制。", "营销活动", IntentType.GENERAL_FAQ);
        addFAQ("会员等级有哪些权益", "普通会员享 9.8 折，银卡会员享 9.5 折，金卡会员享 9 折且免运费，钻石会员享 8.5 折、免运费和专属客服。", "会员权益", IntentType.PRODUCT_INQUIRY);
        addFAQ("如何联系人工客服", "您可以拨打 400-xxx-xxxx 转人工服务，或在 App 内点击'联系客服'选择人工服务。工作时间：周一至周日 9:00-21:00。", "客服相关", IntentType.ESCALATION);
        addFAQ("商品价格是否含税", "页面显示价格均为含税价，您无需额外支付税费。", "支付相关", IntentType.PRODUCT_INQUIRY);
        addFAQ("是否支持上门取件退货", "支持。在提交退货申请时选择'上门取件'，快递员会在约定时间上门收取退货商品。目前该服务覆盖全国 300+ 城市。", "售后服务", IntentType.ORDER_ISSUE);
        addFAQ("账号被冻结了怎么办", "如果您的账号被冻结，请先查看冻结原因通知邮件。如存在疑问请联系客服 400-xxx-xxxx 核实并申诉解冻。", "账户管理", IntentType.TECHNICAL_SUPPORT);
    }

    private void addFAQ(String question, String answer, String category, IntentType intent) {
        faqDatabase.add(new FAQEntry(question, answer, category, intent));
    }

    public List<FAQEntry> searchFAQ(String query) {
        String lowerQuery = query.toLowerCase();
        return faqDatabase.stream()
                .filter(faq -> faq.getQuestion().toLowerCase().contains(lowerQuery)
                        || calculateKeywordSimilarity(faq.getQuestion().toLowerCase(), lowerQuery) > 0.3)
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<FAQEntry> getFAQByIntent(IntentType intent) {
        return faqDatabase.stream()
                .filter(faq -> faq.getIntent() == intent)
                .limit(3)
                .collect(Collectors.toList());
    }

    public String buildKnowledgeContext(String query) {
        List<FAQEntry> results = searchFAQ(query);
        if (results.isEmpty()) {
            return "暂无匹配的知识库内容。";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("以下是与用户问题相关的知识库信息，请参考这些信息来回答用户问题：\n\n");
        for (int i = 0; i < results.size(); i++) {
            FAQEntry entry = results.get(i);
            sb.append("--- 知识片段 ").append(i + 1).append(" ---\n");
            sb.append("问题：").append(entry.getQuestion()).append("\n");
            sb.append("答案：").append(entry.getAnswer()).append("\n");
            sb.append("分类：").append(entry.getCategory()).append("\n\n");
        }
        return sb.toString();
    }

    private double calculateKeywordSimilarity(String text1, String text2) {
        String[] words1 = text1.split("");
        String[] words2 = text2.split("");
        double matchCount = 0;
        for (String w1 : words1) {
            if (text2.contains(w1) && w1.length() > 1) {
                matchCount++;
            }
        }
        return matchCount / Math.max(words1.length, 1);
    }

    public int getFAQCount() {
        return faqDatabase.size();
    }
}
