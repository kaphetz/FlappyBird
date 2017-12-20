package com.kienpt.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Random;

import javax.naming.Context;

public class FlappyBird extends ApplicationAdapter {
    private Stage stage;
    private Stage stage1;
    private boolean isStart = false;
    private Texture myTexture;
    private TextureRegion myTextureRegion;
    private TextureRegionDrawable myTexRegionDrawable;
    private Texture myTexture1;
    private TextureRegion myTextureRegion1;
    private TextureRegionDrawable myTexRegionDrawable1;
    private ImageButton buttonRestart;
    private ImageButton buttonStart;
    private SpriteBatch batch;
    private Texture background;
    private ShapeRenderer mShapeRenderer;
    private int bestScore = 0;

    private Texture gameOver;
    private Texture scoreBoard;

    private Texture[] birds;

    private int flapState = 0;
    private float birdY = 0;
    private float velocity = 0;
    private Circle birdCircle;
    int score = 0;
    int scoringTube = 0;
    String refresh;
    BitmapFont[] fonts;
    private int gameState = 0;
    private float gravity = 2;
    private Texture topTube;
    private Texture bottomTube;
    private float gap = 600;
    private float maxTubeOffset;
    private Random randomGenerator;
    private float tubeVelocity = 6;
    private int numberOfTubes = 4;
    private float[] tubeX = new float[numberOfTubes];
    private float[] tubeOffset = new float[numberOfTubes];
    private float distanceBetweenTubes;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;
    private static GlyphLayout glyphLayout = new GlyphLayout();

    @Override
    public void create() {
        myTexture = new Texture(Gdx.files.internal("restart.png"));
        myTextureRegion = new TextureRegion(myTexture);
        myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        buttonRestart = new ImageButton(myTexRegionDrawable);
        stage = new Stage(new ScreenViewport()); //Set up a stage for the ui
        stage.addActor(buttonRestart); //Add the button to the stage to perform rendering and take input.

        myTexture1 = new Texture(Gdx.files.internal("start.png"));
        myTextureRegion1 = new TextureRegion(myTexture1);
        myTexRegionDrawable1 = new TextureRegionDrawable(myTextureRegion1);
        buttonStart = new ImageButton(myTexRegionDrawable1);
        stage1 = new Stage(new ScreenViewport()); //Set up a stage for the ui
        stage1.addActor(buttonStart); //Add the button to the stage to perform rendering and take input.

        batch = new SpriteBatch();
        birdCircle = new Circle();
        gameOver = new Texture("game_over.png");
        scoreBoard = new Texture("score_board.png");
        refresh = "Tap to refresh game";
        fonts = new BitmapFont[3];
        fonts[0] = new BitmapFont();
        fonts[0].setColor(Color.WHITE);
        fonts[0].getData().setScale(10);
        fonts[1] = new BitmapFont();
        fonts[1].setColor(Color.WHITE);
        fonts[1].getData().setScale(5);
        fonts[2] = new BitmapFont();
        fonts[2].setColor(Color.WHITE);
        fonts[2].getData().setScale(5);
        mShapeRenderer = new ShapeRenderer();
        background = new Texture("bg.png");
        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 5;
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];
        buttonStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameState = 1;
                isStart = true;
                buttonStart.setTouchable(Touchable.disabled);
            }
        });
        buttonRestart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Clicked", "hihi");
                gameState = 1;
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = 0;
                buttonRestart.setTouchable(Touchable.disabled);
            }
        });

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setInputProcessor(stage1);

        startGame();
    }

    private void startGame() {
        birdY = Gdx.graphics.getHeight() / 2 - birds[flapState].getHeight() / 2;
        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() +
                    i * distanceBetweenTubes;
            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void render() {
        //draw
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        if (!isStart) {
            buttonStart.setX(Gdx.graphics.getWidth() / 2 - buttonStart.getWidth() / 2);
            buttonStart.setY(Gdx.graphics.getHeight() / 2 - buttonStart.getHeight() / 2);
            stage1.act(Gdx.graphics.getDeltaTime()); //Perform ui logic
            stage1.draw();
        } else {
            batch.begin();
            if (gameState == 1) {
                if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2 - topTube.getWidth()) {
                    score++;

                    if (scoringTube < numberOfTubes - 1) {
                        scoringTube++;
                    } else {
                        scoringTube = 0;
                    }
                }
                if (Gdx.input.justTouched()) {
                    velocity = -30;
                }
                for (int i = 0; i < numberOfTubes; i++) {
                    if (tubeX[i] < -topTube.getWidth()) {
                        tubeX[i] += numberOfTubes * distanceBetweenTubes;
                        tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                    } else {
                        tubeX[i] -= tubeVelocity;
                    }

                    batch.draw(topTube, tubeX[i],
                            Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                    batch.draw(bottomTube, tubeX[i],
                            Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);


                    topTubeRectangles[i] = new Rectangle(tubeX[i],
                            Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],
                            topTube.getWidth(), topTube.getHeight());
                    bottomTubeRectangles[i] = new Rectangle(tubeX[i],
                            Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],
                            bottomTube.getWidth(), bottomTube.getHeight());
                }

                if (birdY > 0) {
                    velocity += gravity;
                    birdY -= velocity;
                } else {
                    gameState = 2;
                }
            } else if (gameState == 0) {
                if (Gdx.input.justTouched()) {
                    gameState = 1;
                }
            } else if (gameState == 2) {
                bestScore = score > bestScore ? score : bestScore;
                batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2,
                        Gdx.graphics.getHeight() / 2 + gameOver.getHeight() + 100);
                batch.draw(scoreBoard, Gdx.graphics.getWidth() / 2 - scoreBoard.getWidth() / 2,
                        Gdx.graphics.getHeight() / 2 - scoreBoard.getHeight() / 2 - gameOver.getHeight());
                glyphLayout.setText(fonts[1], String.valueOf(score));
                fonts[1].draw(batch, glyphLayout, Gdx.graphics.getWidth() / 2 - glyphLayout.width / 2 - 10,
                        Gdx.graphics.getHeight() / 2 - scoreBoard.getHeight() / 2 + 2 * scoreBoard.getHeight() / 3 - 100);
                glyphLayout.setText(fonts[2], String.valueOf(bestScore));
                fonts[2].draw(batch, glyphLayout,Gdx.graphics.getWidth() / 2 - glyphLayout.width / 2 - 10,
                        Gdx.graphics.getHeight() / 2 - scoreBoard.getHeight() / 2 + scoreBoard.getHeight() / 3 - 100);
      /*      button.setPosition(Gdx.graphics.getWidth() / 2 - button.getWidth() / 2,
                    Gdx.graphics.getHeight() / 2 - scoreBoard.getHeight() / 2 - gameOver.getHeight() - 150);*/
//                buttonRestart.setX(Gdx.graphics.getWidth() / 2 - buttonRestart.getWidth() / 2);
//                buttonRestart.setY(Gdx.graphics.getHeight() / 2 - scoreBoard.getHeight() / 2 - gameOver.getHeight() - 150);
//                stage.act(Gdx.graphics.getDeltaTime()); //Perform ui logic
//                stage.draw();
//                buttonRestart.setTouchable(Touchable.enabled);
                if (Gdx.input.justTouched()) {
                    Gdx.app.log("Restart", "Có");
                    gameState = 1;
                    startGame();
                    score = 0;
                    scoringTube = 0;
                    velocity = 0;
                    velocity = -30;
                }

            }
            if (flapState == 0) {
                flapState = 1;
            } else {
                flapState = 0;
            }

            batch.draw(birds[flapState],
                    Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
            fonts[0].draw(batch, String.valueOf(score), 100, 200);
            batch.end();

            birdCircle = new Circle(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2,
                    birds[flapState].getWidth() / 2);

            // ve hinh
//        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        mShapeRenderer.setColor(Color.RED);
//        mShapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
            for (int i = 0; i < numberOfTubes; i++) {
//            mShapeRenderer.rect(tubeX[i],
//                    Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],
//                    topTube.getWidth(), topTube.getHeight());
//            mShapeRenderer.rect(tubeX[i],
//                    Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],
//                    bottomTube.getWidth(), bottomTube.getHeight());

                // xu ly khi hinh tron va vao hinh chu nhat
                if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) ||
                        Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                    gameState = 2;
                }
            }

        }

//        mShapeRenderer.end();
    }
}
