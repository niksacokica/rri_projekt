package com.projekt.api;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.projekt.map.Geolocation;
import com.projekt.map.PixelPosition;

public class BusStop {
    public Geolocation location;
    public String name;

    public BusStop(Geolocation location, String name) {
        this.location = location;
        this.name = name;
    }

    public void draw(SpriteBatch batch, PixelPosition marker, Texture image, float zoom) {
        batch.draw(image, marker.x-image.getWidth()*zoom*0.5f, marker.y-image.getHeight()*zoom*0.5f, image.getWidth()*zoom, image.getHeight()*zoom);
    }
}
