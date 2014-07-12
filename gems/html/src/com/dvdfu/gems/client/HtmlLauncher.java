package com.dvdfu.gems.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.handlers.Res;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Res.screenWidth, Res.screenHeight);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new MainGame();
        }
}