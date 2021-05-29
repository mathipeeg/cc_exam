package com.example.testapp.DinoDodge;

import android.graphics.Bitmap;

import com.example.testapp.GameEngine;


public class WorldRenderer
{
    GameEngine gameEngine;
    World world;

    Bitmap p1DuckImg;
    Bitmap p1JumpImg;
    Bitmap p1RunImg;
    Bitmap p2DuckImg;
    Bitmap p2JumpImg;
    Bitmap p2RunImg;
    Bitmap rockImg;
    Bitmap meteorImg;
    Bitmap jumpButtonImg;
    Bitmap duckImg;
    float startTime;
    float timer;
    float timer1;
    float timer2;
    int p1Idx = 0;
    int idx = 0;
    int p2DuckIdx = 0;
    int p2Idx = 4;

    public WorldRenderer (GameEngine gameEngine, World world, float deltaTime)
    {
        this.gameEngine = gameEngine;
        this.world = world;
        p1DuckImg = gameEngine.loadBitmap("DinoDodge/p1Duck.png");
        p1JumpImg = gameEngine.loadBitmap("DinoDodge/p1Jump.png");
        p1RunImg = gameEngine.loadBitmap("DinoDodge/p1Run.png");
        p2DuckImg = gameEngine.loadBitmap("DinoDodge/p2Duck.png");
        p2JumpImg = gameEngine.loadBitmap("DinoDodge/p2Jump.png");
        p2RunImg = gameEngine.loadBitmap("DinoDodge/p2Run.png");
        jumpButtonImg = gameEngine.loadBitmap("DinoDodge/JumpButton.png");
        duckImg = gameEngine.loadBitmap("DinoDodge/DuckButton.png");
        rockImg = gameEngine.loadBitmap("DinoDodge/stones_6.png");
        meteorImg = gameEngine.loadBitmap("DinoDodge/meteor.png");
        startTime = deltaTime;
    }

    public void render(float deltaTime)
    {
        if (!world.gameOver)
        {
            timer += deltaTime;

            if (!world.p1Ducking && !world.p1Jumping)
            {
                timer1 = 0;
                idx = 0;
            }
            if (!world.p2Ducking && !world.p2Jumping)
            {
                timer2 = 0;
                p2DuckIdx = 0;
            }

            if(world.p1Alive)
            {
                //Player 1
                if (!world.p1Ducking && !world.p1Jumping)
                {
                    switch (p1Idx)
                    {
                        case 7:
                            gameEngine.drawBitmap(p1RunImg, world.p1Dino.x, world.p1Dino.y - 17, 0, (180 * p1Idx) - 5, 194, 172);
                            break;
                        case 6:
                            gameEngine.drawBitmap(p1RunImg, world.p1Dino.x, world.p1Dino.y - 15, 0, (180 * p1Idx) - 5, 194, 172);
                            break;
                        case 5:
                            gameEngine.drawBitmap(p1RunImg, world.p1Dino.x, world.p1Dino.y - 15, 0, (180 * p1Idx) - 7, 194, 172);
                            break;
                        case 4:
                            gameEngine.drawBitmap(p1RunImg, world.p1Dino.x, world.p1Dino.y - 10, 0, (180 * p1Idx) - 5, 194, 168);
                            break;
                        case 3:
                            gameEngine.drawBitmap(p1RunImg, world.p1Dino.x, world.p1Dino.y - 15, 0, (180 * p1Idx) - 7, 194, 172);
                            break;
                        case 2:
                            gameEngine.drawBitmap(p1RunImg, world.p1Dino.x, world.p1Dino.y - 12, 0, (180 * p1Idx) - 5, 194, 168);
                            break;
                        case 1:
                            gameEngine.drawBitmap(p1RunImg, world.p1Dino.x, world.p1Dino.y - 10, 0, (180 * p1Idx) - 5, 194, 168);
                            break;
                        default:
                            gameEngine.drawBitmap(p1RunImg, world.p1Dino.x, world.p1Dino.y + 1, 0, 10, 194, 160);
                    }
                }

                if (world.p1Ducking && !world.p1Jumping)
                {
                    timer1 += deltaTime;

                    switch (idx)
                    {
                        case 1:
                            gameEngine.drawBitmap(p1DuckImg, world.p1Dino.x, world.p1Dino.y + 9, 0, (180 * idx) - 5, 194, 160);
                            break;
                        case 2:
                            gameEngine.drawBitmap(p1DuckImg, world.p1Dino.x, world.p1Dino.y + 20, 0, (180 * idx) - 15, 194, 130);
                            break;
                        case 3:
                            gameEngine.drawBitmap(p1DuckImg, world.p1Dino.x, world.p1Dino.y + 40, 0, (180 * idx) - 40, 215, 130);
                            break;
                        case 4:
                            gameEngine.drawBitmap(p1DuckImg, world.p1Dino.x, world.p1Dino.y + 52, 0, (180 * idx) + 5, 250, 120);
                            break;
                        default:
                            gameEngine.drawBitmap(p1DuckImg, world.p1Dino.x, world.p1Dino.y, 0, 0, 194, 168);
                    }

                    if (timer1 - startTime > 0.03)
                    {
                        if (idx < 4)
                        {
                            idx++;
                        }
                        timer1 = 0;
                    }
                }

                if (!world.p1Ducking && world.p1Jumping)
                {
                    if (world.p1Velocity > 0 && world.p1Velocity < 400)
                    {
                        gameEngine.drawBitmap(p1JumpImg, world.p1Dino.x, world.p1Dino.y, 0, (180 * 2) - 40, 472, 168);
                    }
                    if (world.p1Velocity > 0 && world.p1Velocity > 400)
                    {
                        gameEngine.drawBitmap(p1JumpImg, world.p1Dino.x, world.p1Dino.y, 0, 180 - 30, 472, 168);
                    }
                    if (world.p1Velocity < 0 && world.p1Velocity > -400)
                    {
                        gameEngine.drawBitmap(p1JumpImg, world.p1Dino.x, world.p1Dino.y, 0, (180 * 2) - 40, 472, 168);
                    }
                    if (world.p1Velocity < 0 && world.p1Velocity < -400)
                    {
                        gameEngine.drawBitmap(p1JumpImg, world.p1Dino.x, world.p1Dino.y, 0, (180 * 3) - 40, 472, 178);
                    }
                }
            }

            if(world.p2Alive)
            {
                //Player 2
                if (!world.p2Ducking && !world.p2Jumping)
                {
                    switch (p2Idx)
                    {
                        case 7:
                            gameEngine.drawBitmap(p2RunImg, world.p2Dino.x, world.p2Dino.y - 17, 0, (180 * p2Idx) - 5, 194, 172);
                            break;
                        case 6:
                            gameEngine.drawBitmap(p2RunImg, world.p2Dino.x, world.p2Dino.y - 15, 0, (180 * p2Idx) - 5, 194, 172);
                            break;
                        case 5:
                            gameEngine.drawBitmap(p2RunImg, world.p2Dino.x, world.p2Dino.y - 15, 0, (180 * p2Idx) - 7, 194, 172);
                            break;
                        case 4:
                            gameEngine.drawBitmap(p2RunImg, world.p2Dino.x, world.p2Dino.y - 10, 0, (180 * p2Idx) - 5, 194, 168);
                            break;
                        case 3:
                            gameEngine.drawBitmap(p2RunImg, world.p2Dino.x, world.p2Dino.y - 15, 0, (180 * p2Idx) - 7, 194, 172);
                            break;
                        case 2:
                            gameEngine.drawBitmap(p2RunImg, world.p2Dino.x, world.p2Dino.y - 12, 0, (180 * p2Idx) - 5, 194, 168);
                            break;
                        case 1:
                            gameEngine.drawBitmap(p2RunImg, world.p2Dino.x, world.p2Dino.y - 10, 0, (180 * p2Idx) - 5, 194, 168);
                            break;
                        default:
                            gameEngine.drawBitmap(p2RunImg, world.p2Dino.x, world.p2Dino.y + 1, 0, 10, 194, 160);
                    }


                }

                if (world.p2Ducking && !world.p2Jumping)
                {
                    timer2 += deltaTime;

                    switch (p2DuckIdx)
                    {
                        case 1:
                            gameEngine.drawBitmap(p2DuckImg, world.p2Dino.x, world.p2Dino.y + 9, 0, (180 * p2DuckIdx) - 5, 194, 160);
                            break;
                        case 2:
                            gameEngine.drawBitmap(p2DuckImg, world.p2Dino.x, world.p2Dino.y + 20, 0, (180 * p2DuckIdx) - 15, 194, 130);
                            break;
                        case 3:
                            gameEngine.drawBitmap(p2DuckImg, world.p2Dino.x, world.p2Dino.y + 40, 0, (180 * p2DuckIdx) - 40, 215, 130);
                            break;
                        case 4:
                            gameEngine.drawBitmap(p2DuckImg, world.p2Dino.x, world.p2Dino.y + 52, 0, (180 * p2DuckIdx) + 5, 250, 120);
                            break;
                        default:
                            gameEngine.drawBitmap(p2DuckImg, world.p2Dino.x, world.p2Dino.y, 0, 0, 194, 168);
                    }

                    if (timer2 - startTime > 0.03)
                    {
                        if (p2DuckIdx < 4)
                        {
                            p2DuckIdx++;
                        }
                        timer2 = 0;
                    }
                }
                if (!world.p2Ducking && world.p2Jumping)
                {
                    if (world.p2Velocity > 0 && world.p2Velocity < 400)
                    {
                        gameEngine.drawBitmap(p2JumpImg, world.p2Dino.x, world.p2Dino.y, 0, (180 * 2) - 40, 472, 170);
                    }
                    if (world.p2Velocity > 0 && world.p2Velocity > 400)
                    {
                        gameEngine.drawBitmap(p2JumpImg, world.p2Dino.x, world.p2Dino.y, 0, 180 - 30, 472, 170);
                    }
                    if (world.p2Velocity < 0 && world.p2Velocity > -400)
                    {
                        gameEngine.drawBitmap(p2JumpImg, world.p2Dino.x, world.p2Dino.y, 0, (180 * 2) - 40, 472, 170);
                    }
                    if (world.p2Velocity < 0 && world.p2Velocity < -400)
                    {
                        gameEngine.drawBitmap(p2JumpImg, world.p2Dino.x, world.p2Dino.y, 0, (180 * 3) - 40, 472, 178);
                    }
                }
            }
            if (timer - startTime > 0.175)
            {
                p1Idx++;
                p2Idx++;
                if (p1Idx == 7)
                {
                    p1Idx = 0;
                }
                if (p2Idx == 7)
                {
                    p2Idx = 0;
                }

                timer = 0;

            }

            gameEngine.drawBitmap(jumpButtonImg, 100, 900);
            gameEngine.drawBitmap(duckImg, 250, 900);
            gameEngine.drawBitmap(jumpButtonImg, 1720, 900);
            gameEngine.drawBitmap(duckImg, 1570, 900);


            for (int i = 0; i < world.blockEnemyList.size(); i++)
            {
                BlockEnemy blockEnemy = world.blockEnemyList.get(i);
                if (blockEnemy.y < 807 - BlockEnemy.HEIGHT)
                {
                    gameEngine.drawBitmap(meteorImg, blockEnemy.x, blockEnemy.y);
                }
                if (blockEnemy.y == 807 - BlockEnemy.HEIGHT)
                {
                    gameEngine.drawBitmap(rockImg, blockEnemy.x, blockEnemy.y);
                }
            }
        }
    }

}
