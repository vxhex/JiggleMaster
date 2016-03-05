package com.vxhex.jigglemaster;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final float JIGGLE_DELTA = 13;

    private int numberOfJiggles = 0;

    private SensorManager _jiggleManager;
    private float _accelerationWithoutGravity;
    private float _accelerationCurrentWithGravity;
    private float _accelerationLastWithGravity;

    private final String[] _healthyBodyHealthyMind = {
            "Way to jiggle it, cadet!",
            "Keep on doing it!",
            "I'm watching you jiggle and it's good!",
            "Healthy jiggles, healthy mind!",
            "You are hard with jiggles!",
            "It jiggles and it looks good!",
            "Let's keep jiggling!",
            "Shake and muscles!",
            "Keep mastering that jiggle!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize the sensor manager
        _jiggleManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _jiggleManager.registerListener(mSensorListener,
                                        _jiggleManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                        SensorManager.SENSOR_DELAY_NORMAL);

        // Initialize accelerations
        _accelerationWithoutGravity = 0.00f;
        _accelerationCurrentWithGravity = SensorManager.GRAVITY_EARTH;
        _accelerationLastWithGravity = SensorManager.GRAVITY_EARTH;
    }

    @Override
    protected void onResume() {
        super.onResume();
        _jiggleManager.registerListener(mSensorListener,
                _jiggleManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        _jiggleManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    private void jiggleUpdate() {
        numberOfJiggles++;
        if (numberOfJiggles % 5 == 0) {
            int random = new Random().nextInt(_healthyBodyHealthyMind.length);
            TextView text = (TextView)findViewById(R.id.textView1);
            text.setText(_healthyBodyHealthyMind[random]);
        }
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            _accelerationLastWithGravity = _accelerationCurrentWithGravity;
            _accelerationCurrentWithGravity = (float) Math.sqrt((double) ((x * x) + (y * y) + (z * z)));

            float accelerationDifference = _accelerationCurrentWithGravity - _accelerationLastWithGravity;
            _accelerationWithoutGravity = _accelerationWithoutGravity * 0.9f + accelerationDifference;

            if (_accelerationWithoutGravity > JIGGLE_DELTA) {
                jiggleUpdate();
                Log.d("JiggleMaster", "Way to jiggle it, cadet! " + numberOfJiggles);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // UGH WHATEVER I DON'T EVEN CARE
        }
    };
}
