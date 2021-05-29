package com.example.testapp.DinoDodge;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.testapp.GameEngine;
import com.example.testapp.Screen;

public class DinoDodge extends GameEngine
{
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Screen createStartScreen()
    {
        music = this.loadMusic("CarScroller/music.ogg");
        return new MainMenuScreenDD(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onResume()
    {
        super.onResume();
        music.play();
        music.setLooping(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onPause()
    {
        super.onPause();
        music.pause();
    }
}
