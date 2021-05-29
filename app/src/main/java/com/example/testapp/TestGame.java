package com.example.testapp;


public class TestGame extends com.example.testapp.GameEngine
{
    @Override
    public com.example.testapp.Screen createStartScreen()
    {
        return new com.example.testapp.TestScreen(this);
    }
}
