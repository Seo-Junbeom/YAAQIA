package com.yaaqia;
import java.util.List;
class StationResponse {
    Response response;

    static class Response {
        Body body;
        Header header;
    }

    static class Body {
        int totalCount;
        List<Item> items;
        int pageNo;
        int numOfRows;
    }

    static class Item {
        double tm;
        String addr;
        String stationName;
    }

    static class Header {
        String resultMsg;
        String resultCode;
    }
}