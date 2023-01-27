package com.projekt.api;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.projekt.map.Geolocation;
import com.projekt.map.PixelPosition;

public class BikeStop {
    public Geolocation location;
    public String name;

    public int numAvailable;
    public int numOfAllParking;

    public BikeStop(Geolocation location, String name, int available, int numOfParking) {
        this.location = location;
        this.name = name;
        this.numAvailable = available;
        this.numOfAllParking = numOfParking;
    }

    public void draw(SpriteBatch batch, PixelPosition marker, Texture image, float zoom) {
        batch.draw(image, marker.x-image.getWidth()*zoom*0.25f, marker.y-image.getHeight()*zoom*0.25f, image.getWidth()*zoom*0.5f, image.getHeight()*zoom*0.5f);
    }

    public String getDescription() {
        return numAvailable + "/" + (numOfAllParking+numAvailable) + " bikes";
    }
}
