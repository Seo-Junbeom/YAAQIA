package com.yaaqia;

import java.util.ArrayList;

public class AirResponse {
    Response response;

    public static class Response {
        Body body;
        Header header;
    }

    public static class Body {
        int totalCount;
        ArrayList<AirItem> items = new ArrayList<AirItem>();
        int pageNo;
        int numOfRows;

    }

    public static class AirItem {
        String so2Grade;
        String coFlag;
        String khaiValue;
        String so2Value;
        String coValue;
        String pm25Flag;
        String pm10Flag;
        String pm10Value;
        String o3Grade;
        String khaiGrade;
        String pm25Value;
        String no2Flag;
        String no2Grade;
        String o3Flag;
        String pm25Grade;
        String so2Flag;
        String dataTime;
        String coGrade;
        String no2Value;
        String pm10Grade;
        String o3Value;

    }

    public static class Header {
        String resultMsg;
        String resultCode;
    }
}

