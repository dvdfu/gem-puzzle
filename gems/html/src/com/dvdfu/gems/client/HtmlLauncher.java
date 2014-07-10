package com.dvdfu.gems.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.handlers.Vars;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Vars.screenWidth, Vars.screenHeight);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new MainGame();
        }
}