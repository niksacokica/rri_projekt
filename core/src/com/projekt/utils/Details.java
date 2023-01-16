package com.projekt.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Details {
    private final Stage stage;
    private final Viewport viewport;
    private final ShapeRenderer renderer;
    private boolean shouldShow;

    private Vector3 pos;
    private final Label title;
    private final Label description;

    public Details(SpriteBatch batch) {
        this.viewport = new FitViewport(900, 900);
        this.renderer = new ShapeRenderer();
        this.stage = new Stage(this.viewport, batch);

        this.shouldShow = false;

        this.pos = new Vector3();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("default.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.genMipMaps = true;
        parameter.size = 18;
        parameter.magFilter = Texture.TextureFilter.MipMapLinearLinear;
        parameter.minFilter = Texture.TextureFilter.MipMapLinearLinear;

        BitmapFont font = generator.generateFont(parameter);
        font.setColor(Color.BLACK);

        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = font;
        ls.fontColor = Color.BLACK;

        this.title = new Label("", ls);

        parameter.size = 11;
        font = generator.generateFont(parameter);

        ls = new Label.LabelStyle();
        ls.font = font;
        ls.fontColor = Color.BLACK;

        this.description = new Label("", ls);

        this.stage.addActor(this.title);
        this.stage.addActor(this.description);

        generator.dispose();
    }

    public void draw(float delta) {
        if(!shouldShow)
            return;

        this.renderer.begin(ShapeRenderer.ShapeType.Filled);
        this.renderer.setColor(Color.GRAY);
        this.renderer.rect(pos.x-viewport.getWorldWidth()*0.1f, pos.y+viewport.getWorldWidth()*0.05f, viewport.getWorldWidth()*0.2f, viewport.getWorldWidth()*0.12f);
        this.renderer.setColor(Color.DARK_GRAY);
        this.renderer.triangle(pos.x-viewport.getWorldWidth()*0.1f, pos.y+viewport.getWorldWidth()*0.05f, pos.x+viewport.getWorldWidth()*0.1f, pos.y+viewport.getWorldWidth()*0.05f, pos.x, pos.y);
        this.renderer.end();

        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        this.renderer.dispose();
        this.stage.dispose();
    }

    public void showDetails(Vector3 pos, String title, String description) {
        this.pos = pos;

        int len = title.length();
        this.title.setText(title.substring(0, (Math.min(15, title.length()))) + (len > 15 ? "..." : ""));
        this.title.setWidth(viewport.getWorldWidth()*0.2f);
        this.title.setPosition(pos.x-viewport.getWorldWidth()*0.1f, pos.y+viewport.getWorldWidth()*0.16f);
        this.title.setAlignment(Align.center);

        len = description.length();
        this.description.setText(description.substring(0, (Math.min(250, description.length()))) + (len > 250 ? "..." : ""));
        this.description.setWidth(viewport.getWorldWidth()*0.2f);
        this.description.setPosition(pos.x-viewport.getWorldWidth()*0.1f, pos.y+viewport.getWorldWidth()*0.1f);
        this.description.setAlignment(Align.center);
        this.description.setWrap(true);

        this.shouldShow = true;
    }

    public void stopShowing() {
        this.shouldShow = false;
    }
}
