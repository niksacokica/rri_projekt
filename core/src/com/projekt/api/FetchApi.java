package com.projekt.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.projekt.Projekt;
import com.projekt.map.Geolocation;

public class FetchApi {
    public void fetchBikes(final Projekt projekt) {
        Net.HttpRequest http = new Net.HttpRequest(Net.HttpMethods.GET);
        http.setUrl("http://localhost:3001/bicycleStations");
        http.setHeader("Content-Type", "text/html; charset=UTF-8");

        Gdx.net.sendHttpRequest(http, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                JsonArray json = JsonParser.parseString(httpResponse.getResultAsString()).getAsJsonArray();
                for(JsonElement je : json){
                    projekt.bikeStops.add(
                            new BikeStop(
                                new Geolocation(
                                        je.getAsJsonObject().get("location").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsDouble(),
                                        je.getAsJsonObject().get("location").getAsJsonObject().get("coordinates").getAsJsonArray().get(1).getAsDouble()
                                ),
                                je.getAsJsonObject().get("name").getAsString(),
                                je.getAsJsonObject().get("numberOfAvailableBicycles").getAsInt(),
                                je.getAsJsonObject().get("numberOfParkingDocks").getAsInt()
                            ));
                }
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("HTTP request failed!");
            }

            @Override
            public void cancelled() {
                System.out.println("HTTP request canceled!");
            }

        });
    }

    public void fetchBuses(final Projekt projekt) {
        Net.HttpRequest http = new Net.HttpRequest(Net.HttpMethods.GET);
        http.setUrl("http://localhost:3001/busStations");
        http.setHeader("Content-Type", "text/html; charset=UTF-8");

        Gdx.net.sendHttpRequest(http, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                JsonArray json = JsonParser.parseString(httpResponse.getResultAsString()).getAsJsonArray();
                for(JsonElement je : json){
                    projekt.busStops.add(
                            new BusStop(
                                    new Geolocation(
                                            je.getAsJsonObject().get("location").getAsJsonObject().get("coordinates").getAsJsonArray().get(1).getAsDouble(),
                                            je.getAsJsonObject().get("location").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsDouble()
                                    ),
                                    je.getAsJsonObject().get("name").getAsString()
                            ));
                }
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("HTTP request failed!");
            }

            @Override
            public void cancelled() {
                System.out.println("HTTP request canceled!");
            }

        });
    }

    public void fetchTrains(final Projekt projekt) {
        Net.HttpRequest http = new Net.HttpRequest(Net.HttpMethods.GET);
        http.setUrl("http://localhost:3001/trainStations");
        http.setHeader("Content-Type", "text/html; charset=UTF-8");

        Gdx.net.sendHttpRequest(http, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                JsonArray json = JsonParser.parseString(httpResponse.getResultAsString()).getAsJsonArray();
                for(JsonElement je : json){
                    projekt.trainStops.add(
                            new TrainStop(
                                    new Geolocation(
                                            je.getAsJsonObject().get("location").getAsJsonObject().get("coordinates").getAsJsonArray().get(1).getAsDouble(),
                                            je.getAsJsonObject().get("location").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsDouble()
                                    ),
                                    je.getAsJsonObject().get("name").getAsString(),
                                    je.getAsJsonObject().get("id").getAsInt(),
                                    je.getAsJsonObject().get("schedule").getAsString(),
                                    je.getAsJsonObject().get("lastModified").getAsString()
                            ));
                }
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("HTTP request failed!");
            }

            @Override
            public void cancelled() {
                System.out.println("HTTP request canceled!");
            }

        });
    }
}
