package com.example.testapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class TestScreen extends com.example.testapp.Screen
{
    com.example.testapp.Sound sound;
    Music backgroundMusic;
    Bitmap red;
    Bitmap green;
    Bitmap button;
    List<TouchEvent> touchEventBuffer;
    boolean pressed = false;
    boolean pressed1 = false;
    boolean pre = false;
    boolean pre1 = false;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TestScreen(com.example.testapp.GameEngine gameEngine)
    {
        super(gameEngine);
        red = gameEngine.loadBitmap("red.png");
        green = gameEngine.loadBitmap("green.png");
        button = gameEngine.loadBitmap("Duck.png");
        sound = gameEngine.loadSound("Breakout/blocksplosion.wav");
        backgroundMusic = gameEngine.loadMusic("Breakout/Glorious_morning.mp3");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void update(float deltaTime)
    {
        gameEngine.clearFrameBuffer(Color.GRAY);

        touchEventBuffer = gameEngine.getTouchEvents();

        gameEngine.drawBitmap(button, 100, 800);
        gameEngine.drawBitmap(button, 1700, 800);


        if (gameEngine.isTouchDown(0) && !pre1)
        {
            if (gameEngine.getTouchX(0) < 200 && gameEngine.getTouchY(0) > 800)
            {
                if (gameEngine.getTouchX(0) > 100 && gameEngine.getTouchY(0) < 900)
                {
                    pre = true;
                }
            }
        }else
        {
            pre = false;
        }

        if (gameEngine.isTouchDown(0) && !pre)
        {
            if (gameEngine.getTouchX(0) < 1800 && gameEngine.getTouchY(0) < 900)
            {
                if (gameEngine.getTouchX(0) > 1700 && gameEngine.getTouchY(0) > 800)
                {
                    pre1 = true;
                }
            }
        }else
        {
            pre1 = false;
        }

        if (gameEngine.isTouchDown(1) && pre)
        {
            if (gameEngine.getTouchX(0) < 1800 && gameEngine.getTouchY(0) < 900)
            {
                if (gameEngine.getTouchX(0) > 1700 && gameEngine.getTouchY(0) > 800)
                {
                    pre1 = true;
                    pre = true;
                }
            }
        }else
        {
            pre1 = false;
            pre = false;
        }



        if (pre)
        {
            pressed = true;
        } else
        {
            pressed = false;
        }
        if (pre1)
        {
            pressed1 = true;
        } else
        {
            pressed1 = false;
        }

        /*
        if (gameEngine.isTouchDown(0))
        {
            if (gameEngine.getTouchX(0) < 1800 && gameEngine.getTouchY(0) > 800)
            {
                if (gameEngine.getTouchX(0) > 1700 && gameEngine.getTouchY(0) < 900)
                {
                    pressed1 = true;
                }
            }
        }


        /*if (gameEngine.isTouchDown(1))
        {
            if (gameEngine.getTouchX(1) < 200 && gameEngine.getTouchY(1) > 800)
            {
                if (gameEngine.getTouchX(1) > 100 && gameEngine.getTouchY(1) < 900)
                {
                    pressed = true;
                }
            }
        }*/











/*        if(gameEngine.isTouchDown(0) && !gameEngine.isTouchDown(1))
        {
            if (gameEngine.getTouchX(0) < 200 && gameEngine.getTouchY(0) > 800)
            {
                if (gameEngine.getTouchX(0) > 100 && gameEngine.getTouchY(0) < 900)
                {
                    pressed = true;
                }
            }
            if (gameEngine.getTouchX(1) < 1800 && gameEngine.getTouchY(1) > 800)
            {
                if (gameEngine.getTouchX(1) > 1700 && gameEngine.getTouchY(1) < 900)
                {
                    pressed1 = true;
                }
            }
        }*/




        /*if(gameEngine.isTouchDown(1))
        {
            if (gameEngine.getTouchX(0) < 1800 && gameEngine.getTouchY(0) > 800)
            {
                if (gameEngine.getTouchX(0) > 1700 && gameEngine.getTouchY(0) < 900)
                {
                    if (gameEngine.getTouchX(1) > 100 && gameEngine.getTouchY(1) > 800)
                    {
                        if (gameEngine.getTouchX(1) < 200 && gameEngine.getTouchY(1) < 900)
                        {
                            Log.d("test", "update: " + gameEngine.getTouchX(0) + " " + gameEngine.getTouchY(0));
                            Log.d("test", "u111    " + gameEngine.getTouchX(1) + " " + gameEngine.getTouchY(1));
                            pressed = true;
                            pressed1 = true;

                        }
                    }
                }
            }

            if (gameEngine.getTouchX(1) < 200 && gameEngine.getTouchY(1) > 800
                    && gameEngine.getTouchX(0) < 1800 && gameEngine.getTouchY(0) > 800)
            {
                if (gameEngine.getTouchX(1) > 100 && gameEngine.getTouchY(1) < 900 &&
                        gameEngine.getTouchX(0) > 1700 && gameEngine.getTouchY(0) < 900)
                {
                    pressed1 = true;
                    pressed = true;
                }
            }
        }*/


        if (pressed)
        {
            gameEngine.drawBitmap(green, 100, 400);
        }else
        {
            gameEngine.drawBitmap(red, 100, 400);
        }

        if (pressed1)
        {
            gameEngine.drawBitmap(green, 1700, 400);
        }else
        {
            gameEngine.drawBitmap(red, 1700, 400);
        }


        //Down do Jump or duck for first press

        //Action_Down do duck

        //Action_Up do jump




        //New GameMechanic! Down: Duck till release

        //On Up do jump

        ////Unless it's been held down for 1 sec ///Wait with hitbox change





    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {

    }

}
