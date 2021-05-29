package com.example.testapp.Breakout;

import java.util.ArrayList;
import java.util.List;

public class World
{
    public static final float MIN_X = 0;
    public static final float MAX_X = 319;
    public static final float MIN_Y = 36;
    public static final float MAX_Y = 479;

    Ball ball = new Ball();
    Paddle paddle = new Paddle();
    List<Block> blocks = new ArrayList<>();
    CollisionListner collisionListner;

    int points = 0;
    int lives = 3;
    int level = 1;
    int hits = 0;

    boolean lostLife = false;
    boolean gameOver = false;

    public World(CollisionListner collisionListner)
    {
        this.collisionListner = collisionListner;
        generateBlocks();
        ball.x = paddle.x + Paddle.WIDTH / 2 - Ball.WIDTH/2;
        ball.y = paddle.y - Ball.HEIGHT - 1;
    }

    public void update(float deltaTime, float accelX, int touchX, boolean isTouchdown)
    {
        if (lostLife)
        {
            //Moves the paddle according to the accelerator
            paddle.x -= accelX * 100 * deltaTime;

            //Move paddle based on touch, only for testing in emulator
            if (isTouchdown)
            {
                paddle.x = touchX - Paddle.WIDTH/2;
            }

            //Makes sure the paddle stays inside the screen
            if (paddle.x < MIN_X)
            {
                paddle.x = MIN_X;
            }
            if (paddle.x + Paddle.WIDTH > MAX_X)
            {
                paddle.x = MAX_X - Paddle.WIDTH;
            }
            ball.x = paddle.x + Paddle.WIDTH / 2 - Ball.WIDTH/2;
            ball.y = paddle.y - Ball.HEIGHT - 1;
            return;
        }
        ball.x += ball.vX * deltaTime;
        ball.y += ball.vY * deltaTime;


        //Ball bounces of the left wall
        if (ball.x < MIN_X)
        {
            ball.vX = -ball.vX;
            ball.x = MIN_X;
            collisionListner.collisionWall();
        }
        //Ball bounces of the right wall
        if (ball.x > MAX_X - Ball.WIDTH)
        {
            ball.vX = -ball.vX;
            ball.x = MAX_X - Ball.WIDTH;
            collisionListner.collisionWall();
        }
        //Ball bounces of the top wall
        if (ball.y < MIN_Y)
        {
            ball.vY = -ball.vY;
            ball.y = MIN_Y;
            collisionListner.collisionWall();
        }

        if (ball.y > MAX_Y - Ball.HEIGHT)
        {
            lostLife = true;
            lives = lives - 1;
            ball.x = paddle.x + Paddle.WIDTH / 2 - Ball.WIDTH/2;
            ball.y = paddle.y - Ball.HEIGHT - 1;
            ball.vY = -Ball.initialSpeed;
            if (lives == 0)
            {
                gameOver = true;
            }
            return;
        }


        //Moves the paddle according to the accelerator
        paddle.x -= accelX * 100 * deltaTime;

        //Move paddle based on touch, only for testing in emulator
        if (isTouchdown)
        {
            paddle.x = touchX - Paddle.WIDTH/2;
        }

        //Makes sure the paddle stays inside the screen
        if (paddle.x < MIN_X)
        {
            paddle.x = MIN_X;
        }
        if (paddle.x + Paddle.WIDTH > MAX_X)
        {
            paddle.x = MAX_X - Paddle.WIDTH;
        }

        if(ball.y > 420)
        {
            checkBallPaddleCollision(deltaTime);
        }
        checkBallBlockCollision(deltaTime);

        if (blocks.size() == 0)
        {
            level++;
            ball.x = 160;
            ball.y = 320 - 40;
            ball.vY = -Ball.initialSpeed*1.3f;
            ball.vX = Ball.initialSpeed*1.3f;
            generateBlocks();
        }

    }

    private void checkBallPaddleCollision(float deltaTime)
    {
        if (ball.y > paddle.y + Paddle.HEIGHT)
        {
            return;
        }
        if ( (ball.x + Ball.WIDTH >= paddle.x) && (ball.x < paddle.x + Paddle.WIDTH) &&
                (ball.y + Ball.HEIGHT > paddle.y) )
        {
            float ballPlacement = ball.x + Ball.WIDTH/2;
            float paddlePlacement = Paddle.WIDTH + paddle.x;

            if(paddlePlacement - ballPlacement > 28)
            {
                ball.vX = -((90.0f/28.0f) * ((paddle.x + 28) - ballPlacement));
            }
            if (paddlePlacement - ballPlacement <= 28){
                ball.vX = (90.0f/28.0f) * (ballPlacement - 28 - paddle.x);
            }

            ball.y = ball.y - ball.vY * deltaTime * 1.01f;
            ball.vY = -ball.vY;
            collisionListner.collisionPaddle();
            hits++;
            if(hits == 5)
            {
                hits = 0;
                if (level == 2)
                {
                    advanceBlocks();
                }
            }
        }
    }

    private void checkBallBlockCollision(float deltaTime)
    {
        Block block = null;
        for (int i = 0; i < blocks.size(); i++)
        {
            block = blocks.get(i);
            if (checkRectCollision(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                    block.x, block.y, Block.WIDTH, Block.HEIGHT))
            {
                blocks.remove(block);
                float oldVX = ball.vX;
                float oldVY = ball.vY;
                reflectBall(ball, block);
                //Back out the ball by 1% to avoid multiple interactions
                ball.x = ball.x - oldVX * deltaTime * 1.01f;
                ball.y = ball.y - oldVY * deltaTime * 1.01f;
                points = points + 10 - block.type;
                collisionListner.collisionBlock();
                break;
            }
        }
    }

    private void reflectBall(Ball ball, Block block)
    {
        //Check if it hits the top left corner
        if (checkRectCollision(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                                block.x, block.y, 1, 1))
        {
            if(ball.vX > 0)
            {
                ball.vX = -ball.vX;
            }
            if(ball.vY < 0)
            {
                ball.vY = -ball.vY;
            }
            return;
        }
        //Check if it hits the top right corner
        if (checkRectCollision(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x + Block.WIDTH - 1, block.y, 1, 1))
        {
            if(ball.vX < 0)
            {
                ball.vX = -ball.vX;
            }
            if(ball.vY > 0)
            {
                ball.vY = -ball.vY;
            }
            return;
        }
        //Check if it hits the bottom left corner
        if(checkRectCollision(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                    block.x, block.y + Block.HEIGHT - 1, 1,1))
        {
            if(ball.vX > 0)
            {
                ball.vX = -ball.vX;
            }
            if(ball.vY < 0)
            {
                ball.vY = -ball.vY;
            }
            return;
        }
        //Check if it hits the bottom right corner
        if(checkRectCollision(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x + Block.WIDTH - 1, block.y + Block.HEIGHT - 1, 1,1))
        {
            if (ball.vX < 0)
            {
                ball.vX = -ball.vX;
            }
            if (ball.vY < 0)
            {
                ball.vY = -ball.vY;
            }
            return;
        }
        //Check collide with TOP rect of block
        if(checkRectCollision(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                    block.x, block.y, Block.WIDTH, 1))
        {
            if(ball.vY > 0)
            {
                ball.vY = -ball.vY;
                return;
            }
        }
        //Check collide with BOTTOM rect of block
        if(checkRectCollision(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x, block.y + Block.HEIGHT - 1, Block.WIDTH, 1))
        {
            if(ball.vY < 0)
            {
                ball.vY = -ball.vY;
                return;
            }
        }
        //Check collide with LEFT SIDE rect of block
        if(checkRectCollision(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x, block.y, 1, Block.HEIGHT))
        {
            if(ball.vX > 0)
            {
                ball.vX = -ball.vX;
                return;
            }
        }
        //Check collide with RIGHT SIDE rect of block
        if(checkRectCollision(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x + Block.WIDTH - 1, block.y, 1, Block.HEIGHT))
        {
            if(ball.vX < 0)
            {
                ball.vX = -ball.vX;
                return;
            }
        }

    }

    private boolean checkRectCollision(float x, float y, float width, float height,
                                       float x2, float y2, float width2, float height2)
    {
        if(x < x2 + width2 && x + width > x2 && y < y2 + height2 && y + height > y2)
        {
            return true;
        }
        return false;
    }

    private void generateBlocks()
    {
        blocks.clear();
        for (int y = 60, type = 0; y < 60 + 8*(Block.HEIGHT + 4); y = y + (int)Block.HEIGHT + 4, type++)
        {
            for (int x = 20; x < 320 - Block.WIDTH; x = x + (int)Block.WIDTH)
            {
                blocks.add(new Block(x, y, type));
            }
        }

    }

    private void advanceBlocks()
    {
        Block block;
        int stop = blocks.size();
        for (int i = 0; i < stop; i++)
        {
            block = blocks.get(i);

            block.y += 10;
        }
    }

}
