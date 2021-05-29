package com.example.testapp.PhilipMiniQuiz;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.example.testapp.GameEngine;
import com.example.testapp.Screen;
import com.example.testapp.TouchEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class QuizScreen extends Screen
{
    Random random = new Random();
    Typeface font;
    Bitmap button;
    String showText = "No text was found";
    List<String> questions = new ArrayList<>();
    List<String> answers = new ArrayList<>();
    int randomQuestion = -1;
    List<TouchEvent> touchEventBuffer;



    public QuizScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        font = gameEngine.loadFont("Breakout/font.ttf");
        button = gameEngine.loadBitmap("Duck.png");
        questions.add("Hvad er as?");
        questions.add("Hvad er wow?");
        answers.add("Godt");
        answers.add("Gammelt");


    }

    @Override
    public void update(float deltaTime)
    {
        gameEngine.clearFrameBuffer(Color.GRAY);

        touchEventBuffer = gameEngine.getTouchEvents();

        if (randomQuestion == -1)
        {
            randomQuestion = random.nextInt(questions.size());
            showText = questions.get(randomQuestion);

        }

        for (int i = 0; i < touchEventBuffer.size(); i++)
        {
            if (touchEventBuffer.get(i).type == TouchEvent.TouchEventType.Down)
            {
                Log.d("", "Down");
                if (gameEngine.getTouchX(0) < 980 / 3 + 100 && gameEngine.getTouchY(0) < 1300)
                {
                    if (gameEngine.getTouchX(0) > 980 / 3 && gameEngine.getTouchY(0) > 1200)
                    {
                        randomQuestion = -1;
                    }
                }
            }
        }



        gameEngine.drawText(font, showText, 20, 100, Color.BLACK, 65);

        gameEngine.drawBitmap(button, 980/3, 1200);
        gameEngine.drawBitmap(button, (980/3) *2, 1200);
        gameEngine.drawBitmap(button, 980/3, 1600);
        gameEngine.drawBitmap(button, (980/3)*2, 1600);












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
