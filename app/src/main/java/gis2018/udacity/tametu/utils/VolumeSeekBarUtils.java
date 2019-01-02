package gis2018.udacity.tametu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.widget.SeekBar;

import gis2018.udacity.tametu.R;

import static gis2018.udacity.tametu.utils.Constants.RINGING_VOLUME_LEVEL_KEY;
import static gis2018.udacity.tametu.utils.Constants.TICKING_VOLUME_LEVEL_KEY;

public class VolumeSeekBarUtils {
    public static int maxVolume;
    public static float floatTickingVolumeLevel;
    public static float floatRingingVolumeLevel;

    /**
     * Set the volume level sliders depending on the last saved values and max volume level
     *
     * @param activity Context
     * @param seekBar  Either a ticking_seek_bar or ringing_seek_bar
     * @return returns seekbar with the set value
     */
    public static SeekBar initializeSeekBar(Activity activity, SeekBar seekBar) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        final AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1); // -1 just because otherwise converttofloat returns infinity
        seekBar.setMax(maxVolume - 1);
        switch (seekBar.getId()) {
            case R.id.ticking_seek_bar:
                seekBar.setProgress(preferences.getInt(TICKING_VOLUME_LEVEL_KEY, maxVolume));
                break;
            case R.id.ringing_seek_bar:
                seekBar.setProgress(preferences.getInt(RINGING_VOLUME_LEVEL_KEY, maxVolume));
                break;
        }
        return seekBar;
    }

    public static float convertToFloat(int currentVolume, int maxVolume) {
        float value;
        value = (float) (1 - (Math.log(maxVolume - currentVolume) / Math.log(maxVolume)));
        return value;
    }

    public void VolumeSeekBarUtils() {
        //Empty
    }
}
