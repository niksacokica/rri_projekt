package com.projekt.api;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.projekt.map.Geolocation;
import com.projekt.map.PixelPosition;

public class TrainStop {
    public Geolocation location;
    public String name;
    public int ID;

    public String schedule;
    public String lastDateModified;

    public TrainStop(Geolocation location, String name, int id, String schedule, String lastModified) {
        this.location = location;
        this.name = name;
        this.ID = id;
        this.schedule = schedule;
        this.lastDateModified = lastModified;
    }

    public void draw(SpriteBatch batch, PixelPosition marker, Texture image, float zoom) {
        batch.draw(image, marker.x-image.getWidth()*zoom*0.25f, marker.y-image.getHeight()*zoom*0.25f, image.getWidth()*zoom*0.5f, image.getHeight()*zoom*0.5f);
    }
}
