package com.projekt.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MapRasterTiles {
    static String mapServiceUrl = "https://api.mapbox.com/v4/";
    static String token = "?access_token=" + "pk.eyJ1Ijoibmlrc2Fjb2tpY2EiLCJhIjoiY2wzaG45bzN1MWRqcTNjcHZrYWE1dXI0byJ9._Le5q5qRLj7ZhiW27l2Yiw";
    static String tilesetId = "mapbox.satellite";
    static String format = "@2x.jpg90";

    final static public int TILE_SIZE = 512;

    public static Texture getRasterTile(int zoom, int x, int y) throws IOException {
        URL url = new URL(mapServiceUrl + tilesetId + "/" + zoom + "/" + x + "/" + y + format + token);
        ByteArrayOutputStream bis = fetchTile(url);
        return getTexture(bis.toByteArray());
    }

    public static Texture getRasterTile(String zoomXY) throws IOException {
        URL url = new URL(mapServiceUrl + tilesetId + "/" + zoomXY + format + token);
        ByteArrayOutputStream bis = fetchTile(url);
        return getTexture(bis.toByteArray());
    }

    public static Texture getRasterTile(ZoomXY zoomXY) throws IOException {
        URL url = new URL(mapServiceUrl + tilesetId + "/" + zoomXY.toString() + format + token);
        ByteArrayOutputStream bis = fetchTile(url);
        return getTexture(bis.toByteArray());
    }

    public static Texture[] getRasterTileZone(ZoomXY zoomXY, int size) throws IOException {
        Texture[] array = new Texture[size * size];
        int[] factorY = new int[size * size];
        int[] factorX = new int[size * size];

        int value = (size - 1) / -2;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                factorY[i * size + j] = value;
                factorX[i + j * size] = value;
            }
            value++;
        }

        for (int i = 0; i < size * size; i++) {
            array[i] = getRasterTile(zoomXY.zoom, zoomXY.x + factorX[i], zoomXY.y + factorY[i]);
            System.out.println(zoomXY.zoom + "/" + (zoomXY.x + factorX[i]) + "/" + (zoomXY.y + factorY[i]));
        }
        return array;
    }

    public static ByteArrayOutputStream fetchTile(URL url) throws IOException {
        ByteArrayOutputStream bis = new ByteArrayOutputStream();
        InputStream is = url.openStream();
        byte[] bytebuff = new byte[4096];
        int n;

        while ((n = is.read(bytebuff)) > 0) {
            bis.write(bytebuff, 0, n);
        }
        return bis;
    }

    public static Texture getTexture(byte[] array) {
        return new Texture(new Pixmap(array, 0, array.length));
    }

    public static ZoomXY getTileNumber(final double lat, final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return new ZoomXY(zoom, xtile, ytile);
    }

    public static double tile2long(int tileNumberX, int zoom) {
        return (tileNumberX / Math.pow(2, zoom) * 360 - 180);
    }

    public static double tile2lat(int tileNumberY, int zoom) {
        double n = Math.PI - 2 * Math.PI * tileNumberY / Math.pow(2, zoom);
        return (180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n))));
    }

    public static double[] project(double lat, double lng, int tileSize) {
        double siny = Math.sin((lat * Math.PI) / 180);

        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);

        return new double[]{
                tileSize * (0.5 + lng / 360),
                tileSize * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI))
        };
    }

    public static PixelPosition getPixelPosition(double lat, double lng, int tileSize, int zoom, int beginTileX, int beginTileY, int height) {
        double[] worldCoordinate = project(lat, lng, tileSize);
        // Scale to fit our image
        double scale = Math.pow(2, zoom);

        // Apply scale to world coordinates to get image coordinates
        return new PixelPosition(
                (int) (Math.floor(worldCoordinate[0] * scale) - (beginTileX * tileSize)),
                height - (int) (Math.floor(worldCoordinate[1] * scale) - (beginTileY * tileSize) - 1)
        );
    }
}
