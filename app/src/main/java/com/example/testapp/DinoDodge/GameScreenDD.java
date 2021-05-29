package com.example.testapp.DinoDodge;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.example.testapp.GameEngine;
import com.example.testapp.Screen;
import com.example.testapp.Sound;
import com.example.testapp.TouchEvent;

import java.util.List;


public class GameScreenDD extends Screen
{
    enum State
    {
        Paused,
        Running,
        GameOver
    }

    World world = null;
    WorldRenderer renderer = null;

    State state = State.Running;
    Bitmap ground;
    Bitmap cloud1;
    Bitmap cloud2;
    Bitmap cloud3;
    Bitmap cloud4;
    Bitmap backGround;
    Bitmap backGroundFront;
    Bitmap backGroundMiddle;
    Bitmap gameOver;
    Bitmap resume;
    float backGroundX = 0;
    float backGroundMiddleX = 0;
    float backGroundFrontX = 0;
    Sound bounceSound;
    Sound crashSound;
    Sound gameoverSound;
    int roadSpeed = 350;
    Typeface font;
    String showText = "No text was found";
    int score = 0;

    public GameScreenDD(GameEngine gameEngine, float deltaTime)
    {
        super(gameEngine);
        ground = gameEngine.loadBitmap("DinoDodge/Ground.png");
        cloud1 = gameEngine.loadBitmap("DinoDodge/clouds_1.png");
        cloud2 = gameEngine.loadBitmap("DinoDodge/clouds_2.png");
        cloud3 = gameEngine.loadBitmap("DinoDodge/clouds_3.png");
        cloud4 = gameEngine.loadBitmap("DinoDodge/clouds_4.png");
        backGround = gameEngine.loadBitmap("DinoDodge/sky-2.png");
        backGroundFront = gameEngine.loadBitmap("DinoDodge/rocks_2-2.png");
        backGroundMiddle = gameEngine.loadBitmap("DinoDodge/rocks_1-2.png");
        gameOver = gameEngine.loadBitmap("CarScroller/gameover.png");
        resume = gameEngine.loadBitmap("CarScroller/resume.png");
        bounceSound = gameEngine.loadSound("CarScroller/bounce.wav");
        crashSound = gameEngine.loadSound("CarScroller/blocksplosion.wav");
        gameoverSound = gameEngine.loadSound("CarScroller/gameover.wav");
        world = new World(gameEngine, new CollisionListener()
        {
            @Override
            public void collisionEdge()
            {
                bounceSound.play(1);
            }

            @Override
            public void collisionMonster()
            {
                crashSound.play(1);
            }

            @Override
            public void gameover()
            {
                gameoverSound.play(1);
            }
        }, roadSpeed);
        renderer = new WorldRenderer(gameEngine, world, deltaTime);
    }

    @Override
    public void update(float deltaTime)
    {
        if (world.gameOver)
        {
            state = State.GameOver;
            pause();
        }

        if(state == State.Paused && gameEngine.getTouchEvents().size() > 0)
        {
            Log.d("GameScreen:", "Starting the game.");
            state = State.Running;
            resume();
        }

        if(state == State.GameOver)
        {
            Log.d("GameSceen", "GameOver!");
            List<TouchEvent> touchEvents = gameEngine.getTouchEvents();
            for (int i = 0; i < touchEvents.size(); i++)
            {
                if(touchEvents.get(i).type == TouchEvent.TouchEventType.Up)
                {
                    gameEngine.setScreen(new MainMenuScreenDD(gameEngine));
                    return;
                }
            }
        }

        if (state == State.Running && gameEngine.getTouchY(0) < 40
                && gameEngine.getTouchX(0) > 320 - 40)
        {
            Log.d("GameScreen", "Pausing the game");
            state = State.Paused;
            pause();
        }

        if (state == State.Running)
        {
            backGroundMiddleX = backGroundMiddleX + (roadSpeed/3.f) * deltaTime;
            backGroundFrontX = backGroundFrontX + (roadSpeed/2.f) * deltaTime;
            backGroundX = backGroundX + (roadSpeed/4.f) * deltaTime;
            if (backGroundX > 3840 - 1920)
            {
                backGroundX = 0;
            }
            if (backGroundMiddleX > 3840 - 1920)
            {
                backGroundMiddleX = 0;
            }
            if (backGroundFrontX > 3840 - 1920)
            {
                backGroundFrontX = 0;
            }
            showText = "Score: " + score;
            score++;
            world.update(deltaTime);
        }
        gameEngine.drawBitmap(backGround, 0, -273, 0, 0, 1920, 1080);
        gameEngine.drawBitmap(backGroundMiddle, 0, -273, (int)backGroundMiddleX, 0, 1920, 1080);
        gameEngine.drawBitmap(cloud1, 0, 0, (int) backGroundX, 0, 1920, 1080);
        gameEngine.drawBitmap(backGroundFront, 0, -273, (int) backGroundFrontX, 0, 1920, 1080);
        gameEngine.drawBitmap(ground, 0, 807, 0, 0, 1920, 1080);
        gameEngine.drawText(font, showText, 720, 940, Color.BLACK, 100);
        renderer.render(deltaTime);

        if (state == State.Paused)
        {
            gameEngine.drawBitmap(resume, 960 - resume.getWidth()/2, 540 - resume.getHeight()/2);
        }

        if(state == State.GameOver)
        {
            gameEngine.drawBitmap(gameOver, 960 - gameOver.getWidth()/2, 540 - gameOver.getHeight()/2);
        }
    }

    @Override
    public void pause()
    {
        if (state == State.Running)
        {
            state = State.Paused;
            gameEngine.music.pause();
        }
    }

    @Override
    public void resume()
    {
        if (state == State.Paused)
        {
            state = State.Running;
            gameEngine.music.play();
        }
    }

    @Override
    public void dispose()
    {

    }
}
