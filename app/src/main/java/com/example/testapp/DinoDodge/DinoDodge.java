package com.example.testapp.DinoDodge;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.testapp.GameEngine;
import com.example.testapp.Screen;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DinoDodge extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        music = this.loadMusic("CarScroller/music.ogg");
        return new MainMenuScreen(this);
    }

    public void onResume()
    {
        super.onResume();
        music.play();
        music.setLooping(true);
    }

    public void onPause() // todo: pause working?
    {
        super.onPause();
        music.pause();
    }
}
