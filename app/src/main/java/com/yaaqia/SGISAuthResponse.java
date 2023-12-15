package com.yaaqia;

public class SGISAuthResponse {
    String id;
    Result result;
    String errMsg;
    int errCd;
    String trId;

    public static class Result {
        String accessTimeout;
        String accessToken;
    }
}
