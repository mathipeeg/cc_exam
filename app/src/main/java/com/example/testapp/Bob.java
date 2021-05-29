package com.example.testapp;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Bob implements com.example.testapp.Mob
{
    Bitmap bitmap;
    int x;
    int y;
    int width = 128;
    int height = 128;
    Rect hitbox;

    @Override
    public int hp()
    {
        return 1;
    }

    @Override
    public Rect hitbox()
    {
        hitbox = new Rect(x,y,width + x,height + y);
        return hitbox;
    }

    @Override
    public String filename()
    {
        return null;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

}
