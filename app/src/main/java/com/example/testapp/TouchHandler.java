package com.example.testapp;

public interface TouchHandler // todo: but... how does it.. know
{
    boolean isTouchDown(int pointer);
    int getTouchX(int pointer);
    int getTouchY(int pointer);
    int getRawTouchX(int pointer);
    int getRawTouchY(int pointer);
}
