package com.example.testapp.PhilipMiniQuiz;


import com.example.testapp.DinoDodge.CollisionListener;
import com.example.testapp.GameEngine;

public class World
{
    public static final float MIN_X = 0;
    public static final float MAX_X = 1079;
    public static final float MIN_Y = 0;
    public static final float MAX_Y = 1920;



    GameEngine gameEngine;


    public World(GameEngine gameEngine, CollisionListener listener, int roadSpeed)
    {
        this.gameEngine = gameEngine;
    }

    public void update(float deltaTime)
    {


    }





}
