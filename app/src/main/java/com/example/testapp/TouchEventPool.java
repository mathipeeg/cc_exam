package com.example.testapp;

public class TouchEventPool extends com.example.testapp.Pool<TouchEvent>
{
    @Override
    protected TouchEvent newItem()
    {
        return new TouchEvent();
    }
}
