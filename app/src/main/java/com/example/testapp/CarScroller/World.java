package com.example.testapp.CarScroller;

import android.util.Log;

import com.example.testapp.GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class World
{
    public static final float MIN_X = 0;
    public static final float MAX_X = 479;
    public static final float MIN_Y = 30;
    public static final float MAX_Y = 285;

    Car car = new Car();
    List<Monster> monsterList = new ArrayList<>();
    public int maxMonsters = 3;

    GameEngine gameEngine;
    CollisionListner listner;

    boolean gameOver = false;
    int points = 0;
    int lives = 0;
    int roadSpeed = 0;

    public World(GameEngine gameEngine, CollisionListner listner, int roadSpeed)
    {
        this.roadSpeed = roadSpeed;
        this.gameEngine = gameEngine;
        this.listner = listner;
        initializeMonsters();
    }

    public void update(float deltaTime, float accelY/*, float accelX*/)
    {
        //Move the car based on accelerometer in the phone
        //car.y = (int) (car.y + accelY * deltaTime * 100);

        //Move the car based on touch. Only for testing!
        if (gameEngine.isTouchDown(0))
        {
            car.y = gameEngine.getTouchY(0) - Car.HEIGHT;
        }

        //Check upper Road boundary
        if(car.y < MIN_Y)
        {
            car.y = (int)MIN_Y;
        }
        if(car.y + Car.HEIGHT> MAX_Y - 1)
        {
            car.y = (int) MAX_Y - Car.HEIGHT - 1;
        }

        Monster monster;
        for (int i = 0; i < maxMonsters; i++)
        {
            monster = monsterList.get(i);
            monster.x -= roadSpeed * deltaTime;
            if (monster.x < 0 - Monster.WIDTH)
            {
                Random random = new Random();
                int randX = random.nextInt(50);
                int randY = random.nextInt(255);
                monster.x = (500 + randX) + i*50;
                monster.y = randY + 30 - Monster.HEIGHT;

            }
        }

        collideCarMonster();

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

    public void collideCarMonster()
    {
        Monster monster;
        for (int i = 0; i < maxMonsters; i++)
        {
            monster = monsterList.get(i);

            if(rectCollision(car.x, car.y, Car.WIDTH, Car.HEIGHT,
                    monster.x, monster.y, Monster.WIDTH, Monster.HEIGHT))
            {
                gameOver = true;
                Log.d("World", "collideCarMonster: GameOver");
            }

        }
    }

    private void initializeMonsters()
    {
        Random random = new Random();
        for (int i = 0; i < maxMonsters; i++)
        {
            int randX = random.nextInt(50);
            int randY = random.nextInt(255);
            Monster monster = new Monster((500 + randX) + i*50, randY + 30 - Monster.HEIGHT);

            monsterList.add(monster);
        }

    }



}
