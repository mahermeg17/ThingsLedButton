package com.megadev.ledbutton;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;

import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String gpioButtonPinName = "BCM21";
    private Button mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyButton();
    }

    private void setupButton() {
        try {
            mButton = new Button(gpioButtonPinName,
                    // high signal indicates the button is pressed
                    // use with a pull-down resistor
                    Button.LogicState.PRESSED_WHEN_LOW
            );
            mButton.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    // do something awesome
                    Log.i(TAG, "Button " + gpioButtonPinName + " pressed " + pressed);
                }
            });
        } catch (IOException e) {
            // couldn't configure the button...
            e.printStackTrace();
        }
    }

    private void destroyButton() {
        if (mButton != null) {
            Log.i(TAG, "Closing button");
            try {
                mButton.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing button", e);
            } finally {
                mButton = null;
            }
        }
    }

}
