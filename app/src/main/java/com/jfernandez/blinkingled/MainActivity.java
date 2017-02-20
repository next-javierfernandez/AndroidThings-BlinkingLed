package com.jfernandez.blinkingled;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final long INTERVAL_BLINK_LED = 500;

    private Gpio led;
    private Handler handler = new Handler();

    private Runnable blinkLed = new Runnable() {
        @Override
        public void run() {
            try {
                if (led != null) {
                    led.setValue(!led.getValue());
                    handler.postDelayed(blinkLed, INTERVAL_BLINK_LED);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to turn on/off the led", e);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            PeripheralManagerService peripheralManager = new PeripheralManagerService();
            led = peripheralManager.openGpio(BoardDefaults.getLedPin());
            led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            handler.post(blinkLed);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to init the led", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(blinkLed);
        try {
            if (led != null) {
                led.close();
                led = null;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to close the led", e);
        }
    }
}
