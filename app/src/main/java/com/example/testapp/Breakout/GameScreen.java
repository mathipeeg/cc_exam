package com.example.testapp.Breakout;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

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

    State state = State.Running;
    Bitmap backGround;
    Bitmap gameOver;
    Bitmap resume;
    Typeface font;
    String showText = "Dummy";
    World world;
    WorldRenderer renderer;
    Sound bounceSound;
    Sound breakBlock;


    public GameScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        backGround = gameEngine.loadBitmap("Breakout/background.png");
        gameOver = gameEngine.loadBitmap("Breakout/gameover.png");
        resume = gameEngine.loadBitmap("Breakout/resume.png");
        font = gameEngine.loadFont("Breakout/font.ttf");
        breakBlock = gameEngine.loadSound("Breakout/blocksplosion.wav");
        bounceSound = gameEngine.loadSound("Breakout/clink_sound.mp3");

        world = new World(new CollisionListner()
        {

            @Override
            public void collisionWall()
            {
                bounceSound.play(1);
            }

            @Override
            public void collisionPaddle()
            {
                bounceSound.play(1);
            }

            @Override
            public void collisionBlock()
            {
                breakBlock.play(1);
            }
        });
        renderer = new WorldRenderer(gameEngine, world);
    }

    @Override
    public void update(float deltaTime)
    {
        if (world.lostLife)
        {
            state = State.Paused;
        }
        if (world.gameOver)
        {
            state = State.GameOver;
        }
        if(state == State.Paused)
        {
            if(world.lostLife)
            {
                world.update(deltaTime, gameEngine.getAccelerometer()[0], gameEngine.getTouchX(0),
                        gameEngine.isTouchDown(0));
            }
            List<TouchEvent> events = gameEngine.getTouchEvents();
            for (int i = 0; i < events.size(); i++)
            {
                if (events.get(i).type == TouchEvent.TouchEventType.Up)
                {

                    world.lostLife = false;
                    state = State.Running;
                    resume();
                    return;
                }
            }
        }
        if (state == State.GameOver)
        {
            List<TouchEvent> events = gameEngine.getTouchEvents();
            for (int i = 0; i < events.size(); i++)
            {
                if (events.get(i).type == TouchEvent.TouchEventType.Up)
                {
                    resume();
                    gameEngine.setScreen(new MainMenuScreen(gameEngine));
                    return;
                }
            }
        }
        if (state == State.Running && gameEngine.getTouchY(0) < 30
                && gameEngine.getTouchX(0) > 290)
        {
            pause();
            state = State.Paused;
            return;
        }
        gameEngine.drawBitmap(backGround,0,0);

        if (state == State.Running)
        {
            world.update(deltaTime, gameEngine.getAccelerometer()[0], gameEngine.getTouchX(0),
                    gameEngine.isTouchDown(0));
        }
        renderer.render();

        showText = "Score: " + world.points + " " + "Lives: " + world.lives;
        gameEngine.drawText(font, showText, 20, 24, Color.GREEN, 15);

        if (state == State.Paused)
        {
            pause();
            gameEngine.drawBitmap(resume, 160 - resume.getWidth()/2, 240 - resume.getHeight()/2);
        }
        if (state == State.GameOver)
        {
            pause();
            gameEngine.drawBitmap(gameOver, 160 - gameOver.getWidth()/2,
                    240 - gameOver.getHeight()/2);
        }



    }

    @Override
    public void pause()
    {
        gameEngine.music.pause();
        if (state == State.Running)
        {
            state = State.Paused;
        }
    }

    @Override
    public void resume()
    {
    gameEngine.music.play();
    }

    @Override
    public void dispose()
    {
        gameEngine.music.pause();
        gameEngine.music.stop();
        gameEngine.music.dispose();
    }
}
