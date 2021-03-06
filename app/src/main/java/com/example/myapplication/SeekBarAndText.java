package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;


public class SeekBarAndText extends AppCompatActivity {
  public int value;
  UsbSerialDevice serialPort;
  String user;
  String sliderName;

  public SeekBarAndText() {
  }

  public SeekBarAndText(SeekBar seekBar, final TextView seekBarValue, final String sliderName) {
    this.sliderName = sliderName;
    seekBar.setProgress(0);

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress,
                                    boolean fromUser) {
        value = progress -60;
        seekBarValue.setText(String.valueOf(value));
        System.out.println(value);

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        user = MainActivity.getUser();
        serialPort = MainActivity.getSerialPort();
        String set = String.format("set dyna vol %s %s %s",
          user, sliderName, String.valueOf(value));

        if (serialPort != null)
          serialPort.write(set.getBytes());

        tvAppend(MainActivity.getSerialView(), String.format("\nset dyna vol %s %s %s",
          user, sliderName, String.valueOf(value)));
      }
    });
  }

  private void tvAppend(TextView tv, CharSequence text) {
    final TextView ftv = tv;
    final CharSequence ftext = text;

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ftv.setText(ftext);
      }
    });
  }



}
