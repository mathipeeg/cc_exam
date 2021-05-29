package com.example.testapp.PhilipMiniQuiz;

import android.graphics.Bitmap;

import com.example.testapp.GameEngine;


public class WorldRenderer
{
    GameEngine gameEngine;
    World world;
    Bitmap playerImg;
    Bitmap playerImg1;
    Bitmap player1Img;
    Bitmap player1Img1;
    Bitmap blockImg;
    Bitmap jumpImg;
    Bitmap duckImg;

    //int worldSize = world.blocks.size();

    public WorldRenderer(GameEngine gameEngine, World world)
    {
        this.gameEngine = gameEngine;
        this.world = world;
        playerImg = gameEngine.loadBitmap("DinoDodge/Run1.png");
        playerImg1 = gameEngine.loadBitmap("DinoDodge/Duck1.png");
        player1Img = gameEngine.loadBitmap("DinoDodge/Run1.2.png");
        player1Img1 = gameEngine.loadBitmap("DinoDodge/Duck1.2.png");
        jumpImg = gameEngine.loadBitmap("DinoDodge/JumpButton.png");
        duckImg = gameEngine.loadBitmap("DinoDodge/DuckButton.png");
        blockImg = gameEngine.loadBitmap("DinoDodge/BlockF.png");
    }

    public void render()
    {

    }

}
