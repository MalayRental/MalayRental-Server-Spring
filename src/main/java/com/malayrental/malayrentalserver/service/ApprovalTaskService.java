package com.malayrental.malayrentalserver.service;

import java.util.Map;

public interface ApprovalTaskService {
    int createApproval(Map<String, Object> data);
    int finishApproval(Map<String, Object> data);
    int getApprovalList(Map<String, Object> data, java.util.List<Map<String, Object>> resultList);
} 