package com.example.testapp.CarScroller;

import android.graphics.Bitmap;

import com.example.testapp.GameEngine;


public class WorldRenderer
{
    GameEngine gameEngine;
    World world;
    Bitmap carImg;
    Bitmap monsterImg;

    //int worldSize = world.blocks.size();

    public WorldRenderer (GameEngine gameEngine, World world)
    {
        this.gameEngine = gameEngine;
        this.world = world;
        carImg = gameEngine.loadBitmap("CarScroller/xbluecar2.png");
        monsterImg = gameEngine.loadBitmap("CarScroller/xyellowmonster2.png");
    }

    public void render()
    {
        gameEngine.drawBitmap(carImg, world.car.x, world.car.y);

        for (int i = 0; i < world.monsterList.size(); i++)
        {
            gameEngine.drawBitmap(monsterImg, world.monsterList.get(i).x, world.monsterList.get(i).y);
        }
    }

}
