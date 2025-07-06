package com.malayrental.malayrentalserver.websocket;

public class ChatMessageResult {
    private String response;      // 给发送方的响应
    private String targetUserId;  // 需要推送的对方userId
    private String pushContent;   // 推送给对方的内容

    public ChatMessageResult() {}

    public ChatMessageResult(String response, String targetUserId, String pushContent) {
        this.response = response;
        this.targetUserId = targetUserId;
        this.pushContent = pushContent;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getPushContent() {
        return pushContent;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }
} 