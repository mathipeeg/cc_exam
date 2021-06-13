package com.example.testapp;

public class TouchEventPool extends Pool<TouchEvent>
{
    @Override
    protected TouchEvent newItem()
    {
        return new TouchEvent();
    } // todo: why protected? also what is pool omg
}
