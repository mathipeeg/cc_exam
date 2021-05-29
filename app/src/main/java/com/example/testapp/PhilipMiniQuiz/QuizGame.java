package com.example.testapp.PhilipMiniQuiz;

import com.example.testapp.GameEngine;
import com.example.testapp.Screen;

public class QuizGame extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        return new QuizScreen(this);
    }
}
