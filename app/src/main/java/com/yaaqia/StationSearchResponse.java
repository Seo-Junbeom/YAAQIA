package com.yaaqia;

import java.util.List;

public class StationSearchResponse {

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
        String dmX;
        String item;
        String mangName;
        String year;
        String addr;
        String stationName;
        String dmY;
    }

    static class Header {
        String resultMsg;
        String resultCode;
    }
}
