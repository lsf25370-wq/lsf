package com.csa.assistant.service;

import com.csa.assistant.entity.ReturnRequestEntity;
import com.csa.assistant.mapper.ReturnRequestMapper;
import com.csa.assistant.model.ReturnRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnRequestService {

    private final ReturnRequestMapper returnRequestMapper;

    public ReturnRequestService(ReturnRequestMapper returnRequestMapper) {
        this.returnRequestMapper = returnRequestMapper;
    }

    public ReturnRequest getByRequestId(String requestId) {
        ReturnRequestEntity entity = returnRequestMapper.selectByRequestId(requestId);
        return entity != null ? toModel(entity) : null;
    }

    public ReturnRequest getByOrderId(String orderId) {
        ReturnRequestEntity entity = returnRequestMapper.selectByOrderId(orderId);
        return entity != null ? toModel(entity) : null;
    }

    public List<ReturnRequest> getUserRequests(String userId) {
        return returnRequestMapper.selectByUserId(userId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<ReturnRequest> getAllRequests() {
        return returnRequestMapper.selectAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReturnRequest submitReturnRequest(String orderId, String reason, String description, Double refundAmount, String userId) {
        ReturnRequestEntity existing = returnRequestMapper.selectByOrderId(orderId);
        if (existing != null) {
            return toModel(existing);
        }
        ReturnRequestEntity entity = new ReturnRequestEntity();
        entity.setRequestId("RET" + System.currentTimeMillis());
        entity.setOrderId(orderId);
        entity.setReason(reason);
        entity.setDescription(description);
        entity.setStatus("SUBMITTED");
        entity.setRefundAmount(refundAmount);
        entity.setUserId(userId);
        entity.setCreateTime(java.time.LocalDateTime.now().toString());
        returnRequestMapper.insert(entity);
        return toModel(entity);
    }

    @Transactional
    public ReturnRequest reviewRequest(String requestId, boolean approved) {
        ReturnRequestEntity entity = returnRequestMapper.selectByRequestId(requestId);
        if (entity == null || !"SUBMITTED".equals(entity.getStatus())) {
            return null;
        }
        entity.setStatus(approved ? "APPROVED" : "REJECTED");
        entity.setProcessedTime(java.time.LocalDateTime.now().toString());
        returnRequestMapper.update(entity);
        return toModel(entity);
    }

    @Transactional
    public ReturnRequest completeReturn(String requestId) {
        ReturnRequestEntity entity = returnRequestMapper.selectByRequestId(requestId);
        if (entity == null || !"APPROVED".equals(entity.getStatus())) {
            return null;
        }
        entity.setStatus("COMPLETED");
        entity.setProcessedTime(java.time.LocalDateTime.now().toString());
        returnRequestMapper.update(entity);
        return toModel(entity);
    }

    public String getReturnSummary(String userId) {
        List<ReturnRequestEntity> requests = returnRequestMapper.selectByUserId(userId);
        long pendingCount = requests.stream()
                .filter(r -> "SUBMITTED".equals(r.getStatus()) || "REVIEWING".equals(r.getStatus()))
                .count();
        long completedCount = requests.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .count();
        return "## 售后统计\n- 待处理：" + pendingCount
                + "\n- 已完成：" + completedCount
                + "\n- 总申请：" + requests.size();
    }

    private ReturnRequest toModel(ReturnRequestEntity entity) {
        ReturnRequest request = new ReturnRequest();
        request.setId(entity.getId());
        request.setRequestId(entity.getRequestId());
        request.setOrderId(entity.getOrderId());
        request.setReason(entity.getReason());
        request.setDescription(entity.getDescription());
        request.setStatus(entity.getStatus());
        request.setRefundAmount(entity.getRefundAmount());
        request.setCreateTime(entity.getCreateTime());
        request.setProcessedTime(entity.getProcessedTime());
        request.setUserId(entity.getUserId());
        return request;
    }
}
