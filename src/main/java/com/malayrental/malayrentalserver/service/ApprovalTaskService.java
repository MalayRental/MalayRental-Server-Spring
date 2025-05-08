package com.malayrental.malayrentalserver.service;

import java.util.Map;

public interface ApprovalTaskService {
    int createApproval(Map<String, Object> data);
    int finishApproval(Map<String, Object> data);
} 