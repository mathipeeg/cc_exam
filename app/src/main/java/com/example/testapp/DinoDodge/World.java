package com.example.testapp.DinoDodge;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.testapp.GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    public int maxEnemies = 4;
    List<BlockEnemy> blockEnemyList = new ArrayList<>();

    public World(GameEngine gameEngine, CollisionListener listener, int roadSpeed)
    {
        this.roadSpeed = roadSpeed;
        this.gameEngine = gameEngine;
        this.listener = listener;
        initializeEnemies();
    }

    public void update(float deltaTime)
    {

        if(!p1Alive)
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

            if (p1Dino.y + Dino.HEIGHT > MAX_Y - 1) // todo: what this do?
            {
                p1Jumping = false;
                p1Dino.y = (int) MAX_Y - Dino.HEIGHT;
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

        BlockEnemy blockEnemy;
        BlockEnemy prevBlockEnemy;
        for (int i = 0; i < maxEnemies; i++)
        {
            blockEnemy = blockEnemyList.get(i);

            if (i == 0) // todo: pls forklar alt med previous (også l. 119 og videre)
            {
                prevBlockEnemy = blockEnemyList.get(i + maxEnemies - 1);
            } else
            {
                prevBlockEnemy = blockEnemyList.get(i-1);
            }

            blockEnemy.x -= roadSpeed * deltaTime;

            if (blockEnemy.x < -BlockEnemy.WIDTH)
            {
                Random random = new Random();
                boolean isItABird = random.nextBoolean();
                int randX = random.nextInt(250);
                int randY = random.nextInt(220) + 20;

                if (1920 - getLatestEnemyX(i) < 150) { // todo: tilføjer 150 til x koordinat, hvis den er for tæt på forrige?
                    randX += 150 - (1920 - getLatestEnemyX(i));
                }

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
        }
    }

    private void initializeEnemies()
    {
        Random random = new Random();

        for (int i = 0; i < maxEnemies; i++)
        {
            boolean isItABird = random.nextBoolean();
            int randX = random.nextInt(250) + 150;
            int randY = random.nextInt(220) + 20;

            if(!isItABird)
            {
                BlockEnemy blockEnemy = new BlockEnemy((1920 + randX) + i * 450 + BlockEnemy.WIDTH, 807 - BlockEnemy.HEIGHT);

                blockEnemyList.add(blockEnemy);
            }else
            {
                BlockEnemy blockEnemy = new BlockEnemy((1920 + randX) + i * 450 + BlockEnemy.WIDTH, 807 - BlockEnemy.HEIGHT - randY);

                blockEnemyList.add(blockEnemy);
            }
        }
    }

    private int getLatestEnemyX(int currentIndex) {
        // finder den seneste fjende i listen
        int latestIndex = 0;

        if (currentIndex == 0) {
            latestIndex = 3;
        } else if (currentIndex == 3) {
            latestIndex = 1;
        } else {
            latestIndex = currentIndex - 1;
        }

        if (blockEnemyList == null) {
            return 0;
        }

        return blockEnemyList.get(latestIndex).x;
    }
}
