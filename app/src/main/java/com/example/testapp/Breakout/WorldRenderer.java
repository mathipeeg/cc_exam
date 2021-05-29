package com.example.testapp.Breakout;

import android.graphics.Bitmap;

import com.example.testapp.GameEngine;


public class WorldRenderer
{
    GameEngine gameEngine;
    World world;
    Bitmap ballImg;
    Bitmap paddleImg;
    Bitmap blockImg;
    Block block;
    //int worldSize = world.blocks.size();

    public WorldRenderer (GameEngine gameEngine, World world)
    {
        this.gameEngine = gameEngine;
        this.world = world;
        ballImg = gameEngine.loadBitmap("Breakout/ball.png");
        paddleImg = gameEngine.loadBitmap("Breakout/paddle.png");
        blockImg = gameEngine.loadBitmap("Breakout/blocks.png");
    }

    public void render()
    {
        gameEngine.drawBitmap(ballImg, (int)world.ball.x, (int)world.ball.y);
        gameEngine.drawBitmap(paddleImg, (int)world.paddle.x, (int)world.paddle.y);
        for (int i = 0; i < world.blocks.size(); i++)
        {
            block = world.blocks.get(i);
            gameEngine.drawBitmap(blockImg, (int)block.x, (int)block.y,
                    0, (int)(block.type * Block.HEIGHT),
                    (int)Block.WIDTH, (int)Block.HEIGHT);
        }
    }

}
