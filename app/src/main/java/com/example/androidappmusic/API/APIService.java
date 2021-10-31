package com.example.androidappmusic.API;

public class APIService {
    private static final String url =  "https://muzike2.000webhostapp.com/Server/";

    public static DataService getService() { // Dữ liệu trả về cho DataService
        // Gửi cấu hình lên
        return APIRetrofitClient.getClient(url).create(DataService.class);
    }
}
