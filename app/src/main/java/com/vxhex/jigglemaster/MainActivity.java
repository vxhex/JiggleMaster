package com.vxhex.jigglemaster;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private final float JIGGLE_DIFFERENTIAL = 13;

    private SensorManager _jiggleManager;
    private float _accelerationWithoutGravity;
    private float _accelerationCurrentWithGravity;
    private float _accelerationLastWithGravity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            _accelerationLastWithGravity = _accelerationCurrentWithGravity;
            _accelerationCurrentWithGravity = (float) Math.sqrt((double) ((x * x) + (y * y) + (z * z)));

            float accelerationDifference = _accelerationCurrentWithGravity - _accelerationLastWithGravity;
            _accelerationWithoutGravity = _accelerationWithoutGravity * 0.9f + accelerationDifference;

            if (_accelerationWithoutGravity > JIGGLE_DIFFERENTIAL) {
                Log.d("JiggleMaster", "Way to jiggle it, cadet!");
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // UGH WHATEVER I DON'T EVEN CARE
        }
    };
}
