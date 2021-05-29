package com.example.testapp.DinoDodge;

import android.util.Log;

import com.example.testapp.GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class World
{
    GameEngine gameEngine;
    CollisionListener listener;

    public static final float MIN_X = 0;
    public static final float MAX_X = 1919;
    public static final float MIN_Y = 150;
    public static final float MAX_Y = 807;

    boolean gameOver = false;
    int roadSpeed;

    Dino p1Dino = new Dino();
    boolean p1Alive = true;
    boolean p1Ducking = false;
    boolean p1Jumping = false;
    int p1Velocity = 0;

    Dino p2Dino = new Dino();
    boolean p2Alive = true;
    boolean p2Ducking = false;
    boolean p2Jumping = false;
    int p2Velocity = 0;

    public int maxEnemies = 4;
    List<BlockEnemy> blockEnemyList = new ArrayList<>();

    public World(GameEngine gameEngine, CollisionListener listener, int roadSpeed)
    {
        this.roadSpeed = roadSpeed;
        this.gameEngine = gameEngine;
        this.listener = listener;
        initializeEnemies();
        p2Dino.x = p2Dino.x + 270;
    }

    public void update(float deltaTime)
    {

        if(!p1Alive && !p2Alive)
        {
            gameOver = true;
        }
        if(p1Alive)
        {
            //Player 1 jump movement
            p1Ducking = false;

            if (p1Jumping)
            {
                p1Dino.y -= p1Velocity * deltaTime;
                p1Velocity -= p1Dino.gravity * deltaTime;
            }

            if (p1Dino.y + Dino.HEIGHT > MAX_Y - 1)
            {
                p1Jumping = false;
                p1Dino.y = (int) MAX_Y - Dino.HEIGHT;
            }

            //Player 1
            if (gameEngine.volDown && !p1Jumping)
            {
                p1Ducking = true;
            }

            if (gameEngine.volUp && !p1Jumping)
            {
                p1Velocity = p1Dino.velocity;
                p1Jumping = true;
            }

            if (gameEngine.isTouchDown(0) && !gameEngine.isTouchDown(1) && !p1Jumping && gameEngine.getTouchX(0) < 400)
            {
                if (gameEngine.getTouchX(0) < 200 && gameEngine.getTouchY(0) > 900)
                {
                    if (gameEngine.getTouchX(0) > 100 && gameEngine.getTouchY(0) < 1000)
                    {
                        p1Velocity = p1Dino.velocity;
                        p1Jumping = true;
                    }
                }
                if (gameEngine.getTouchX(0) < 350 && gameEngine.getTouchY(0) > 900)
                {
                    if (gameEngine.getTouchX(0) > 250 && gameEngine.getTouchY(0) < 1000)
                    {
                        p1Ducking = true;
                    }
                }
            }
        }

        //Player 2 Jump movement
        p2Ducking = false;

        if(p2Jumping)
        {
            p2Dino.y -= p2Velocity * deltaTime;
            p2Velocity -= p2Dino.gravity * deltaTime;
        }

        if(p2Dino.y + Dino.HEIGHT > MAX_Y - 1)
        {
            p2Jumping = false;
            p2Dino.y = (int) MAX_Y - Dino.HEIGHT;
        }

        //Player 2
        if (gameEngine.isTouchDown(0) && !p2Jumping)
        {
            if(!gameEngine.isTouchDown(1))
            {
                if (gameEngine.getTouchX(0) < 1820 && gameEngine.getTouchY(0) > 900)
                {
                    if (gameEngine.getTouchX(0) > 1720 && gameEngine.getTouchY(0) < 1000)
                    {
                        p2Velocity = p2Dino.velocity;
                        p2Jumping = true;
                    }
                }
                if (gameEngine.getTouchX(0) < 1670 && gameEngine.getTouchY(0) > 900)
                {
                    if (gameEngine.getTouchX(0) > 1570 && gameEngine.getTouchY(0) < 1000)
                    {
                        p2Ducking = true;
                    }
                }
            }
        }


        BlockEnemy blockEnemy;
        BlockEnemy prevBlockEnemy;
        for (int i = 0; i < maxEnemies; i++)
        {
            blockEnemy = blockEnemyList.get(i);

            if (i == 0)
            {
                prevBlockEnemy = blockEnemyList.get(i + maxEnemies - 1);
            } else
            {
                prevBlockEnemy = blockEnemyList.get(i-1);
            }

            blockEnemy.x -= roadSpeed * deltaTime;
            if (blockEnemy.x < 0 - BlockEnemy.WIDTH)
            {
                Random random = new Random();
                boolean isItABird = random.nextBoolean();
                int randX = random.nextInt(200) + 100;
                int randY = random.nextInt(220) + 20;

                if(!isItABird)
                {
                    if (prevBlockEnemy.x < 1920)
                    {
                        blockEnemy.x = (1920 + randX) + random.nextInt(100) + BlockEnemy.WIDTH;
                        blockEnemy.y = 807 - BlockEnemy.HEIGHT;
                    } else
                    {
                        blockEnemy.x = (1920 + randX) + (prevBlockEnemy.x - 1920) + 200 + BlockEnemy.WIDTH;
                        blockEnemy.y = 807 - BlockEnemy.HEIGHT;
                    }
                } else
                {
                    if (prevBlockEnemy.x < 1920)
                    {
                        blockEnemy.x = (1920 + randX) + random.nextInt(100) + BlockEnemy.WIDTH;
                        blockEnemy.y = 807 - BlockEnemy.HEIGHT - randY;
                    } else
                    {
                        blockEnemy.x = (1920 + randX) + (prevBlockEnemy.x - 1920) + 200 + BlockEnemy.WIDTH;
                        blockEnemy.y = 807 - BlockEnemy.HEIGHT - randY;
                    }
                }
            }
        }
        collideDinoEnemy();
    }

    private boolean rectCollision(float x, float y, float width, float height,
                                  float x2, float y2, float width2, float height2)
    {
        if(x < x2 + width2 && x + width > x2 && y < y2 + height2 && y + height > y2)
        {
            return true;
        }
        return false;
    }

    public void collideDinoEnemy()
    {
        BlockEnemy blockEnemy;
        for (int i = 0; i < maxEnemies; i++)
        {
            blockEnemy = blockEnemyList.get(i);

            if (p1Alive)
            {
                if (p1Ducking)
                {
                    if (rectCollision(p1Dino.x, p1Dino.y + 100, Dino.WIDTH, Dino.HEIGHT,
                            blockEnemy.x, blockEnemy.y, BlockEnemy.WIDTH, BlockEnemy.HEIGHT))
                    {
                        p1Alive = false;
                    }
                } else
                {
                    if (rectCollision(p1Dino.x + (Dino.WIDTH / 3.f), p1Dino.y, (Dino.WIDTH / 3.f), Dino.HEIGHT,
                            blockEnemy.x, blockEnemy.y, BlockEnemy.WIDTH, BlockEnemy.HEIGHT))
                    {
                        p1Alive = false;
                    }
                }
            }

            if (p2Alive)
            {
                if (p2Ducking)
                {
                    if (rectCollision(p2Dino.x, p2Dino.y + 100, Dino.WIDTH, Dino.HEIGHT,
                            blockEnemy.x, blockEnemy.y, BlockEnemy.WIDTH, BlockEnemy.HEIGHT))
                    {
                        p2Alive = false;
                    }
                } else
                {
                    if (rectCollision(p2Dino.x + (Dino.WIDTH / 3.f), p2Dino.y, (Dino.WIDTH / 3.f), Dino.HEIGHT,
                            blockEnemy.x, blockEnemy.y, BlockEnemy.WIDTH, BlockEnemy.HEIGHT))
                    {
                        p2Alive = false;
                    }
                }
            }
        }
    }

    private void initializeEnemies()
    {
        Random random = new Random();

        for (int i = 0; i < maxEnemies; i++)
        {
            boolean isItABird = random.nextBoolean();
            Log.d("", "" + isItABird);

            if(!isItABird)
            {
                int randX = random.nextInt(300) + 100;
                BlockEnemy blockEnemy = new BlockEnemy((1920 + randX) + i * 450 + BlockEnemy.WIDTH, 807 - BlockEnemy.HEIGHT);

                blockEnemyList.add(blockEnemy);
            }else
            {
                int randX = random.nextInt(300) + 100;
                int randY = random.nextInt(220) + 20;
                BlockEnemy blockEnemy = new BlockEnemy((1920 + randX) + i * 450 + BlockEnemy.WIDTH, 807 - BlockEnemy.HEIGHT - randY);

                blockEnemyList.add(blockEnemy);
            }
        }
    }
}
