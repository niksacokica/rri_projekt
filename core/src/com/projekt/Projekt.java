package com.projekt;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import com.projekt.api.BikeStop;
import com.projekt.api.BusStop;
import com.projekt.api.FetchApi;
import com.projekt.api.TrainStop;
import com.projekt.map.Geolocation;
import com.projekt.map.MapRasterTiles;
import com.projekt.map.PixelPosition;
import com.projekt.map.ZoomXY;
import com.projekt.utils.Details;
import com.projekt.utils.Filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Projekt extends ApplicationAdapter implements GestureDetector.GestureListener {
    private Vector3 touchPosition;

    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;

    private Texture[] mapTiles;
    private ZoomXY beginTile;

    private Texture bikeStopImage;
    private Texture busStopImage;
    private Texture trainStopImage;

    public List<BikeStop> bikeStops = new ArrayList<>();
    public List<BusStop> busStops = new ArrayList<>();
    public List<TrainStop> trainStops = new ArrayList<>();

    private SpriteBatch batch;
    private Filter filter;
    private Details details;

    private final int NUM_TILES = 10;
    private final int ZOOM = 14;
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.57, 15.63);
    private final int WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
    private final int HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.position.set(WIDTH / 2f, HEIGHT / 2f, 0);
        camera.viewportWidth = WIDTH / 2f;
        camera.viewportHeight = HEIGHT / 2f;
        camera.zoom = 0.25f;
        camera.update();

        bikeStopImage = new Texture("bike.png");
        busStopImage = new Texture("bus.png");
        trainStopImage = new Texture("train.png");

        touchPosition = new Vector3();
        Gdx.input.setInputProcessor(new GestureDetector(this));

        try {
            ZoomXY centerTile = MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, ZOOM);
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, NUM_TILES);
            beginTile = new ZoomXY(ZOOM, centerTile.x - ((NUM_TILES - 1) / 2), centerTile.y - ((NUM_TILES - 1) / 2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        TiledMap tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(NUM_TILES, NUM_TILES, MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE);
        int index = 0;
        for (int j = NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();

        FetchApi fetch = new FetchApi();
        fetch.fetchBikes(this);
        fetch.fetchBuses(this);
        fetch.fetchTrains(this);

        details = new Details(batch);
        filter = new Filter(batch, details);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
            float zoomVal = 0.33f;

            if(filter.showBikes){
                for(BikeStop stop : bikeStops) {
                    PixelPosition marker = MapRasterTiles.getPixelPosition(stop.location.lat, stop.location.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

                    stop.draw(batch, marker, bikeStopImage, camera.zoom * zoomVal);
                }
            }

            if(filter.showBuses){
                for(BusStop stop : busStops) {
                    PixelPosition marker = MapRasterTiles.getPixelPosition(stop.location.lat, stop.location.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

                    stop.draw(batch, marker, busStopImage, camera.zoom * zoomVal);
                }
            }

            if(filter.showTrains){
                for(TrainStop stop : trainStops) {
                    PixelPosition marker = MapRasterTiles.getPixelPosition(stop.location.lat, stop.location.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

                    stop.draw(batch, marker, trainStopImage, camera.zoom * zoomVal);
                }
            }
        batch.end();

        details.draw(Gdx.graphics.getDeltaTime());
        filter.draw(Gdx.graphics.getDeltaTime());

        handleInput();
    }

    @Override
    public void dispose() {
        busStopImage.dispose();
        bikeStopImage.dispose();
        trainStopImage.dispose();

        details.dispose();
        filter.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        touchPosition.set(x, y, 0);
        camera.unproject(touchPosition);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        camera.zoom += initialDistance >= distance ? 0.2f : -0.2f;

        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {
    }

    private void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;

            details.stopShowing();
        }if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;

            details.stopShowing();
        }if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);

            details.stopShowing();
        }if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);

            details.stopShowing();
        }if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);

            details.stopShowing();
        }if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);

            details.stopShowing();
        }if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && Gdx.input.getX() > 900*0.15f&& Gdx.input.getY() > 900*0.15f) {
            String[] result = getClosestStop(camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)));

            PixelPosition marker;
            switch(result[0]) {
                case "bike":
                    marker = MapRasterTiles.getPixelPosition(bikeStops.get(Integer.parseInt(result[1])).location.lat, bikeStops.get(Integer.parseInt(result[1])).location.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

                    details.showDetails(camera.project(new Vector3(marker.x, marker.y, 0)), bikeStops.get(Integer.parseInt(result[1])).name, bikeStops.get(Integer.parseInt(result[1])).getDescription());
                    break;
                case "bus":
                    marker = MapRasterTiles.getPixelPosition(busStops.get(Integer.parseInt(result[1])).location.lat, busStops.get(Integer.parseInt(result[1])).location.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

                    details.showDetails(camera.project(new Vector3(marker.x, marker.y, 0)), busStops.get(Integer.parseInt(result[1])).name, "");
                    break;
                case "train":
                    marker = MapRasterTiles.getPixelPosition(trainStops.get(Integer.parseInt(result[1])).location.lat, trainStops.get(Integer.parseInt(result[1])).location.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

                    details.showDetails(camera.project(new Vector3(marker.x, marker.y, 0)), trainStops.get(Integer.parseInt(result[1])).name, trainStops.get(Integer.parseInt(result[1])).schedule);
                    break;
                default:
                    details.stopShowing();
            }
        }

        camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 0.5f);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, HEIGHT - effectiveViewportHeight / 2f);
    }

    private String[] getClosestStop(Vector3 clickPos) {
        String stop = "";
        int ind = -1;
        float dist = Float.MAX_VALUE;

        if(filter.showBikes){
            for(int i=0; i<bikeStops.size(); i++) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(bikeStops.get(i).location.lat, bikeStops.get(i).location.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

                float tempDist = (float) Math.sqrt(Math.pow(marker.x-clickPos.x, 2)+Math.pow(marker.y-clickPos.y, 2));
                if(tempDist < dist){
                    dist = tempDist;
                    stop = "bike";
                    ind = i;
                }
            }
        }

        if(filter.showBuses){
            for(int i=0; i<busStops.size(); i++) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(busStops.get(i).location.lat, busStops.get(i).location.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

                float tempDist = (float) Math.sqrt(Math.pow(marker.x-clickPos.x, 2)+Math.pow(marker.y-clickPos.y, 2));
                if(tempDist < dist){
                    dist = tempDist;
                    stop = "bus";
                    ind = i;
                }
            }
        }

        if(filter.showTrains){
            for(int i=0; i<trainStops.size(); i++) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(trainStops.get(i).location.lat, trainStops.get(i).location.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

                float tempDist = (float) Math.sqrt(Math.pow(marker.x-clickPos.x, 2)+Math.pow(marker.y-clickPos.y, 2));
                if(tempDist < dist){
                    dist = tempDist;
                    stop = "train";
                    ind = i;
                }
            }
        }

        return dist * camera.zoom > 9 ? new String[]{"", "-1"} :  new String[]{stop, String.valueOf(ind)};
    }
}
