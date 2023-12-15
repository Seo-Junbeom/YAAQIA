package com.yaaqia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;



import android.widget.Toast;


import androidx.core.app.ActivityCompat;





public class MainActivity extends AppCompatActivity {
    private String accessToken;
    private Double latitude;
    private Double longitude;
    private Double posX;
    private Double posY;
    static RequestQueue requestQueue;

    private static final String SERVICE_KEY_ENC = "?serviceKey=5QqcJzlUEHF1zwZAqf%2BLxUgZADIKLPlVGML836RWx9%2BAH3sCx4xbVlo8JSafqztkCf22M181Zy6EVMQ0GsnuLQ%3D%3D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button refreshButton = findViewById(R.id.search);
        refreshButton.setOnClickListener(v -> {
            EditText stationEditText = findViewById(R.id.station_id);
            String stationName = stationEditText.getText().toString().trim();

            makeAirRequest(stationName);
            makeSearchRequest(stationName);
        });

        Button locationButton = findViewById(R.id.current_location);
        locationButton.setOnClickListener(v -> getLocation());


        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

    }

    public void makeAirRequest(String stationName) {
        String airParameters = "&returnType=json&numOfRows=1&pageNo=1&stationName=" + stationName +"&dataTerm=DAILY&ver=1.0";
        String url = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty" + SERVICE_KEY_ENC + airParameters;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d("@@@@@@@@@@@@@@@@@", "응답 ->1 " + response);

                    airResponse(response);

                },
                error -> Log.d("@@@@@@@@@@@@@@@@@", "에러 ->1 " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {

                return new HashMap<>();
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

        Log.d("@@@@@@@@@@@@@@@@@", "1요청 보냄.");
    }

    public void airResponse(String response) {
        Gson gson = new Gson();
        AirResponse airResponse = gson.fromJson(response, AirResponse.class);


        if (airResponse != null && airResponse.response != null && airResponse.response.body != null) {
            ArrayList<AirResponse.AirItem> airItems = airResponse.response.body.items;


            if (airItems.size() > 0) {
                AirResponse.AirItem airItem = airItems.get(0);
                updateAirValues(R.id.pm10value, airItem.pm10Value, airItem.pm10Grade);
                updateAirValues(R.id.pm25value, airItem.pm25Value, airItem.pm25Grade);
                updateAirValues(R.id.so2value, airItem.so2Value, airItem.so2Grade);
                updateAirValues(R.id.covalue, airItem.coValue, airItem.coGrade);
                updateAirValues(R.id.o3value, airItem.o3Value, airItem.o3Grade);
                updateAirValues(R.id.no2value, airItem.no2Value, airItem.no2Grade);
            }
        }
    }
    private void updateAirValues(int textViewId, String text, String grade) {
        TextView textView = findViewById(textViewId);
        textView.setText(text);

        int color;

        if (grade != null) {

            switch (grade) {
                case "1":
                    color = ContextCompat.getColor(this, android.R.color.holo_green_light);
                    break;
                case "2":
                    color = ContextCompat.getColor(this, android.R.color.holo_orange_light);
                    break;
                case "3":
                    color = ContextCompat.getColor(this, android.R.color.holo_orange_dark);
                    break;
                case "4":
                    color = ContextCompat.getColor(this, android.R.color.holo_red_light);
                    break;
                default:
                    color = ContextCompat.getColor(this, android.R.color.black);
            }
        } else {
            color = ContextCompat.getColor(this, android.R.color.black);
        }

            textView.setTextColor(color);
        }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            return;
        }


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {


            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();


                    Toast.makeText(MainActivity.this,
                            "Latitude: " + latitude + "\nLongitude: " + longitude,
                            Toast.LENGTH_SHORT).show();
                            makeSGISRequest();


                    locationManager.removeUpdates(this);
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                }
            };


            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener);

        } else {

            Toast.makeText(this, "Please enable GPS or network location services",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void makeSGISRequest() {
        String url = "https://sgisapi.kostat.go.kr/OpenAPI3/auth/authentication.json?consumer_key=4b538e3792614fe89464&consumer_secret=22d153974fe1461fae13";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d("@@@@@@@@@@@@@@@@@", "응답 -> 2" + response);

                    SGISResponse(response);
                },
                error -> Log.d("@@@@@@@@@@@@@@@@@", "에러 -> 2" + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {

                return new HashMap<>();
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

        Log.d("@@@@@@@@@@@@@@@@@", "요청 보냄.2");
    }

    public void SGISResponse(String response) {
        Gson gson = new Gson();
        SGISAuthResponse authResponse = gson.fromJson(response, SGISAuthResponse.class);

        if (authResponse != null) {
            SGISAuthResponse.Result result = authResponse.result;

            if (result != null) {
                accessToken = result.accessToken;
                makeConvertRequest();
            }
        }
    }


    public void makeConvertRequest() {
        String url = "https://sgisapi.kostat.go.kr/OpenAPI3/transformation/transcoord.json?src=4326&dst=5181&accessToken="+accessToken+"&posX="+longitude+"&posY="+latitude;
        Log.d("@@@@", url);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d("@@@@@@@@@@@@@@@@@", "응답 -> " + response);
                    ConvertResponse(response);
                },
                error -> Log.d("@@@@@@@@@@@@@@@@@", "에러 ->3 " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {

                return new HashMap<>();
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

        Log.d("@@@@@@@@@@@@@@@@@", "요청 보냄.");
    }

    public void ConvertResponse(String response) {
        Gson gson = new Gson();
        TM tm = gson.fromJson(response, TM.class);

        posX = tm.result.posX;
        posY = tm.result.posY;

        makeStationRequest();
    }

    public void makeStationRequest() {
        String stationParameters = "&returnType=json&tmX="+posX+"&tmY="+posY;
        String url = "https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList" + SERVICE_KEY_ENC + stationParameters;
        Log.d("@@@@", url);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d("@@@@@@@@@@@@@@@@@", "응답 s-> " + response);
                    stationResponse(response);
                },
                error -> Log.d("@@@@@@@@@@@@@@@@@", "에러 ->3 " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {

                return new HashMap<>();
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

        Log.d("@@@@@@@@@@@@@@@@@", "요청 보냄.");
    }
    @SuppressLint("SetTextI18n")
    public void stationResponse(String response) {
        Gson gson = new Gson();
        StationResponse stationResponse = gson.fromJson(response, StationResponse.class);

        if (stationResponse != null && stationResponse.response != null
                && stationResponse.response.body != null && !stationResponse.response.body.items.isEmpty()) {

            StationResponse.Item closest = stationResponse.response.body.items.get(0);
            Log.d("@@@", "측정소 명: " + closest.stationName);
            Log.d("@@@", "거리: " + closest.tm);
            Log.d("@@@", "측정소 주소: " + closest.addr);

            TextView stationNameTextView = findViewById(R.id.station_name);
            TextView stationAddrTextView = findViewById(R.id.station_addr);
            TextView kmTextView = findViewById(R.id.km);

            stationNameTextView.setText("측정소 명: " + closest.stationName);
            stationAddrTextView.setText("측정소 주소: " + closest.addr);
            kmTextView.setText("거리: " + closest.tm + "km");

            EditText stationEditText = findViewById(R.id.station_id);
            stationEditText.setText(closest.stationName);
            makeAirRequest(closest.stationName);
        }
    }


    public void makeSearchRequest(String stationName) {
        String searchParameters = "&returnType=json&numOfRows=1&pageNo=1&stationName=" + stationName;
        String url = "https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getMsrstnList" + SERVICE_KEY_ENC + searchParameters;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d("@@@@@@@@@@@@@@@@@", "응답 ->1 " + response);

                    searchResponse(response);

                },
                error -> Log.d("@@@@@@@@@@@@@@@@@", "에러 ->1 " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {

                return new HashMap<>();
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

        Log.d("@@@@@@@@@@@@@@@@@", "1요청 보냄.");
    }

    @SuppressLint("SetTextI18n")
    public void searchResponse(String response) {
        Gson gson = new Gson();
        StationSearchResponse ssResponse = gson.fromJson(response, StationSearchResponse.class);

        if (ssResponse != null && ssResponse.response != null
                && ssResponse.response.body != null && !ssResponse.response.body.items.isEmpty()) {

            StationSearchResponse.Item firstItem = ssResponse.response.body.items.get(0);

            TextView stationNameTextView = findViewById(R.id.station_name);
            TextView addrTextView = findViewById(R.id.station_addr);
            TextView kmTextView = findViewById(R.id.km);

            stationNameTextView.setText("측정소 명: " + firstItem.stationName);
            addrTextView.setText("측정소 주소: " + firstItem.addr);
            kmTextView.setText("거리: ");

            }
        }
    }
