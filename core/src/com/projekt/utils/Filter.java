package com.projekt.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Filter {
    private final Viewport viewport;
    private final Stage stage;
    private final ShapeRenderer renderer;
    private final Details details;

    public boolean showBikes;
    public boolean showBuses;
    public boolean showTrains;

    private final Skin skin;

    public Filter(SpriteBatch batch, Details details) {
        this.viewport = new FitViewport(900, 900);
        this.stage = new Stage(this.viewport, batch);
        this.renderer = new ShapeRenderer();
        this.details = details;

        this.showBikes = true;
        this.showBuses = true;
        this.showTrains = true;

        this.skin = new Skin(Gdx.files.internal("neon-ui.json"));
        this.stage.addActor(createButtons());

        Gdx.input.setInputProcessor(this.stage);
    }

    public void draw(float delta) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.GRAY);
        renderer.rect(0, 0, viewport.getWorldWidth()*0.16f, viewport.getWorldWidth()*0.12f);
        renderer.end();

        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        this.stage.dispose();
        this.skin.dispose();
        this.renderer.dispose();
    }

    private Actor createButtons() {
        Table table = new Table();
        table.defaults();

        CheckBox showBikesCheck = new CheckBox("Show Bikes", skin);
        showBikesCheck.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showBikes = !showBikes;

                details.stopShowing();
            }
        });

        CheckBox showBusesCheck = new CheckBox("Show Buses", skin);
        showBusesCheck.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showBuses = !showBuses;

                details.stopShowing();
            }
        });

        CheckBox showTrainsCheck = new CheckBox("Show Trains", skin);
        showTrainsCheck.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showTrains = !showTrains;

                details.stopShowing();
            }
        });

        showBikesCheck.setChecked(true);
        showBusesCheck.setChecked(true);
        showTrainsCheck.setChecked(true);

        table.add(showBikesCheck).fill().row();
        table.add(showBusesCheck).fill().row();
        table.add(showTrainsCheck).fill();

        table.padLeft(viewport.getWorldWidth()*0.15f);
        table.padBottom(viewport.getWorldWidth()*0.12f);

        return table;
    }
}
