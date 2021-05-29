package com.example.testapp.CarScroller;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.testapp.GameEngine;
import com.example.testapp.Screen;
import com.example.testapp.Sound;
import com.example.testapp.TouchEvent;

import java.util.List;


public class GameScreen extends Screen
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
    Bitmap backGround;
    Bitmap gameOver;
    Bitmap resume;
    float backGroundX = 0;
    Sound bounceSound;
    Sound crashSound;
    Sound gameoverSound;
    int roadSpeed = 100;

    public GameScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        backGround = gameEngine.loadBitmap("CarScroller/xcarbackground.png");
        gameOver = gameEngine.loadBitmap("CarScroller/gameover.png");
        resume = gameEngine.loadBitmap("CarScroller/resume.png");
        bounceSound = gameEngine.loadSound("CarScroller/bounce.wav");
        crashSound = gameEngine.loadSound("CarScroller/blocksplosion.wav");
        gameoverSound = gameEngine.loadSound("CarScroller/gameover.wav");
        world = new World(gameEngine, new CollisionListner()
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

            @Override
            public void coin()
            {

            }
        }, roadSpeed);
        renderer = new WorldRenderer(gameEngine, world);

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
                    gameEngine.setScreen(new MainMenuScreen(gameEngine));
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
            backGroundX = backGroundX + roadSpeed * deltaTime;
            if (backGroundX > 2700 - 480)
            {
                backGroundX = 0;
            }
            world.update(deltaTime, gameEngine.getAccelerometer()[1]);
        }
        gameEngine.drawBitmap(backGround, 0, 0, (int)backGroundX, 0, 480, 320);
        renderer.render();

        if (state == State.Paused)
        {
            gameEngine.drawBitmap(resume, 240 - resume.getWidth()/2, 160 - resume.getHeight()/2);
        }

        if(state == State.GameOver)
        {
            gameEngine.drawBitmap(gameOver, 240 - gameOver.getWidth()/2, 160 - gameOver.getHeight()/2);
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
