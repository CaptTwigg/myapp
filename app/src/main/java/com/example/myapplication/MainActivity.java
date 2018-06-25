package com.example.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
  public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
  UsbManager usbManager;
  UsbDevice device;
  static UsbSerialDevice serialPort;
  UsbDeviceConnection connection;
  static TextView serialView;
  static String user = "1";
  int value;


  UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
    @Override
    public void onReceivedData(byte[] arg0) {
      String data = null;
      try {
        data = new String(arg0, "UTF-8");
        data.concat("/n");
        //tvAppend(serialView, data+"\n");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
  };

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
        boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
        if (granted) {
          connection = usbManager.openDevice(device);
          serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
          if (serialPort != null) {
            if (serialPort.open()) { //Set Serial Connection Parameters.
              serialPort.setBaudRate(9600);
              serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
              serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
              serialPort.setParity(UsbSerialInterface.PARITY_NONE);
              serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
              serialPort.read(mCallback);
              tvAppend(serialView, "\nSerial Connection Opened\n");

            } else {
              Log.d("SERIAL", "PORT NOT OPEN");
            }
          } else {
            Log.d("SERIAL", "PORT IS NULL");
          }
        } else {
          Log.d("SERIAL", "PERM NOT GRANTED");
        }
      } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
        onClickStart();
      } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
        onClickStop();
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
    serialView = findViewById(R.id.serialView);
    serialView.setMovementMethod(new ScrollingMovementMethod());
    IntentFilter filter = new IntentFilter();
    filter.addAction(ACTION_USB_PERMISSION);
    filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
    registerReceiver(broadcastReceiver, filter);

    new SeekBarAndText((SeekBar) findViewById(R.id.sidetoneSeekBar), (TextView) findViewById(R.id.sidetoneTextView), "sidetone");
    new SeekBarAndText((SeekBar) findViewById(R.id.TG0SeekBar), (TextView) findViewById(R.id.TG0TextView), "tg0");
    new SeekBarAndText((SeekBar) findViewById(R.id.TG1SeekBar), (TextView) findViewById(R.id.TG1TextView), "tg1");
    new SeekBarAndText((SeekBar) findViewById(R.id.TG2SeekBar), (TextView) findViewById(R.id.TG2TextView), "tg2");
    new SeekBarAndText((SeekBar) findViewById(R.id.comg1SeekBar), (TextView) findViewById(R.id.comg1TextView), "comg1");
    new SeekBarAndText((SeekBar) findViewById(R.id.comg2SeekBar), (TextView) findViewById(R.id.comg2TextView), "comg2");
    new SeekBarAndText((SeekBar) findViewById(R.id.comg3SeekBar), (TextView) findViewById(R.id.comg3TextView), "comg3");
    new SeekBarAndText((SeekBar) findViewById(R.id.comg4SeekBar), (TextView) findViewById(R.id.comg4TextView), "comg4");


  }

  public void onClickStart() {

    HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
    if (!usbDevices.isEmpty()) {
      boolean keep = true;
      for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
        device = entry.getValue();
        int deviceVID = device.getVendorId();
        if (deviceVID == 0x2A03)//Arduino Vendor ID
        {
          PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
          usbManager.requestPermission(device, pi);
          keep = false;
        } else {
          connection = null;
          device = null;
        }

        if (!keep)
          break;
      }
    }
  }

  public void onClickStop() {
    serialPort.close();
    tvAppend(serialView, "\nSerial Connection Closed\n");
  }

  public void onClickSend(View view) {
    if (serialPort != null)
      serialPort.write("hej".getBytes());
  }

  public void printSerial(String stringToPrint) {
    serialPort.write(stringToPrint.getBytes());
  }


  private void tvAppend(TextView tv, CharSequence text) {
    final TextView ftv = tv;
    final CharSequence ftext = text;

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ftv.append(ftext);
      }
    });
  }


  public void onRadioButtonClicked(View view) {
    // Is the button now checked?
    boolean checked = ((RadioButton) view).isChecked();

    // Check which radio button was clicked
    switch (view.getId()) {
      case R.id.user1:
        if (checked) {
          user = "1";
        }
        break;
      case R.id.user2:
        if (checked) {
          user = "2";
        }
        break;
      case R.id.user3:
        if (checked) {
          user = "3";
        }
        break;
      case R.id.user4:
        if (checked) {
          user = "4";
        }
        break;
      case R.id.user5:
        if (checked) {
          user = "5";
        }
        break;
    }
  }

  public static String getUser() {
    return user;
  }

  public static TextView getSerialView() {
    return serialView;
  }
  public static UsbSerialDevice getSerialPort() {
    return serialPort;
  }
}
