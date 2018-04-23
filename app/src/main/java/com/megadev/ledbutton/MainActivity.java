package com.megadev.ledbutton;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String gpioLedPinName = "BCM6";
    private Gpio mLedGpio;
    private static final String gpioButtonPinName = "BCM21";
    private Button mButton;
    private ToggleButton ledToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupButton();
        setupLed();
        setContentView(R.layout.activity_main);
        ledToggle = (ToggleButton) findViewById(R.id.led_toggle);
        ledToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "ledToggle is ON");
                    setLedValue(isChecked);
                } else {
                    setLedValue(isChecked);
                    Log.i(TAG, "ledToggle is OFF");
                }
            }
        });
        ledToggle.setChecked(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyButton();
        destroyLed();
    }

    private void setupLed() {
        PeripheralManager pioService = PeripheralManager.getInstance();
        try {
            Log.i(TAG, "Configuring GPIO pins");
            mLedGpio = pioService.openGpio(gpioLedPinName);
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            Log.e(TAG, "Error configuring GPIO pins", e);
        }
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
                    setLedValue(pressed);
                }
            });
        } catch (IOException e) {
            // couldn't configure the button...
            e.printStackTrace();
        }
    }

    /**
     * Update the value of the LED output.
     */
    private void setLedValue(boolean value) {
        try {
            mLedGpio.setValue(value);
        } catch (IOException e) {
            Log.e(TAG, "Error updating GPIO value", e);
        }
    }

    private void destroyLed() {
        if (mLedGpio != null) {
            try {
                mLedGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing LED GPIO", e);
            } finally {
                mLedGpio = null;
            }
            mLedGpio = null;
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
