package com.example.testapp;

public interface TouchHandler
{
    boolean isTouchDown(int pointer);
    int getTouchX(int pointer);
    int getTouchY(int pointer);
    int getRawTouchX(int pointer);
    int getRawTouchY(int pointer);
}
