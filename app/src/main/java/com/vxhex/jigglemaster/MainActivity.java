package com.vxhex.jigglemaster;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final float JIGGLE_DELTA = 13;

    private int _numberOfJiggles = 0;

    private SensorManager _jiggleManager;
    private float _accelerationWithoutGravity;
    private float _accelerationCurrentWithGravity;
    private float _accelerationLastWithGravity;

    private SoundPool _soundPool;
    private int _soundIDs[]  = new int[5];
    private int _soundsLoaded = 0;

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
        initializeJiggle();
        initializeSounds();
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

    private void initializeJiggle() {
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

    private void initializeSounds() {
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        _soundPool = new SoundPool(_soundIDs.length, AudioManager.STREAM_MUSIC, 0);
        _soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                ++_soundsLoaded;
            }
        });
        _soundIDs[0] = _soundPool.load(this, R.raw.sound1, 1);
        _soundIDs[1] = _soundPool.load(this, R.raw.sound2, 1);
        _soundIDs[2] = _soundPool.load(this, R.raw.sound3, 1);
        _soundIDs[3] = _soundPool.load(this, R.raw.sound4, 1);
        _soundIDs[4] = _soundPool.load(this, R.raw.sound5, 1);
    }

    private void jiggleUpdate() {
        _numberOfJiggles++;
        if (_numberOfJiggles % 3 == 0) {
            // Set a random encouragement
            int random = new Random().nextInt(_healthyBodyHealthyMind.length);
            TextView text = (TextView)findViewById(R.id.textView1);
            text.setText(_healthyBodyHealthyMind[random]);

            // Play a random noise
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;
            if (_soundsLoaded == _soundIDs.length) {
                random = new Random().nextInt(_soundIDs.length);
                _soundPool.play(_soundIDs[random], volume, volume, 1, 0, 1f);
            }
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
                Log.d("JiggleMaster", "Way to jiggle it, cadet! " + _numberOfJiggles);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // UGH WHATEVER I DON'T EVEN CARE
        }
    };
}
