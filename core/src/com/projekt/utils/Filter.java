package com.projekt.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.projekt.Projekt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Filter {
    private final Viewport viewport;
    private final Stage stage;
    private final ShapeRenderer renderer;
    private final Details details;
    private final Projekt projekt;
    private final AudioRecorder recorder;

    public boolean showBikes;
    public boolean showBuses;
    public boolean showTrains;

    private final Texture micImage;

    private final Skin skin;

    public Filter(Projekt projekt, SpriteBatch batch, Details details) {
        this.viewport = new FitViewport(900, 900);
        this.stage = new Stage(this.viewport, batch);
        this.renderer = new ShapeRenderer();
        this.details = details;
        this.projekt = projekt;
        this.recorder = Gdx.audio.newAudioRecorder(16000, true);

        this.showBikes = true;
        this.showBuses = true;
        this.showTrains = true;

        this.micImage = new Texture("mic.png");

        this.skin = new Skin(Gdx.files.internal("neon-ui.json"));
        this.stage.addActor(createButtons());

        Gdx.input.setInputProcessor(this.stage);
    }

    public void draw(float delta) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.GRAY);
        renderer.rect(0, 0, viewport.getWorldWidth()*0.16f, viewport.getWorldWidth()*0.16f);
        renderer.end();

        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        this.stage.dispose();
        this.skin.dispose();
        this.renderer.dispose();
        this.micImage.dispose();
        this.recorder.dispose();
    }

    private Actor createButtons() {
        Table table = new Table();
        table.defaults();

        ImageButton mic = new ImageButton(new TextureRegionDrawable(new TextureRegion(this.micImage)));
        mic.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final short[] data = new short[16000];
                        recorder.read(data, 0, data.length);

                        ByteBuffer buffer = ByteBuffer.allocate(44 + data.length * 2);
                        buffer.order(ByteOrder.LITTLE_ENDIAN);

                        buffer.putInt(0x46464952);
                        buffer.putInt(0);
                        buffer.putInt(0x45564157);
                        buffer.putInt(0x20746d66);
                        buffer.putInt(16);
                        buffer.putShort((short) 1);
                        buffer.putShort((short) 1);
                        buffer.putInt(16000);
                        buffer.putInt(32000);
                        buffer.putShort((short) 2);
                        buffer.putShort((short) 16);
                        buffer.putInt(0x61746164);
                        buffer.putInt(data.length * 2);

                        for (short sample : data) {
                            buffer.putShort(sample);
                        }

                        buffer.putInt(4, buffer.capacity() - 8);

                        byte[] wavData = buffer.array();
                        FileHandle file = Gdx.files.local("sound.wav");
                        //file.writeBytes(wavData, false);

                        ProcessBuilder processBuilder = new ProcessBuilder("python", "model.py");
                        try {
                            Process process = processBuilder.start();

                            BufferedReader stdInput
                                    = new BufferedReader(new InputStreamReader(
                                    process.getInputStream()));
                            String s;
                            while((s = stdInput.readLine()) != null) {
                                projekt.selectByVoice(s);
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

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

        table.add(mic).width(viewport.getWorldWidth()*0.03f).height(viewport.getWorldWidth()*0.1f).row();
        table.add(showBikesCheck).fill().row();
        table.add(showBusesCheck).fill().row();
        table.add(showTrainsCheck).fill();

        table.padLeft(viewport.getWorldWidth()*0.15f);
        table.padBottom(viewport.getWorldWidth()*0.18f);

        return table;
    }
}
