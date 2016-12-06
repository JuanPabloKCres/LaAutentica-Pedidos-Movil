package com.example.suiza.pedidos_en_sony;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.example.suiza.pedidos_en_sony.MainActivity;


public class AndroidLauncher extends MainActivity {
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    // Aqui guardaremos las coordenadas del acelerómetro local
    public static float accelerometerX, accelerometerY, accelerometerZ;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVERABLE_BT = 0;
    private BluetoothSocket btSocket = null;
    private DataOutputStream outStream = null;
    private static String serverAddress = "00:15:83:0C:BF:EB";
    private static final UUID BLUETOOTH_SPP_UUID = UUID
            .fromString("70AF804F-3913-4889-8CB4-8243E0A4475C");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // INICIO APP ANDROID
    }



    /*Comprobar si dispositivo Android soporta Bluetooth*/
    public static boolean supportsBluetooth() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }


    /*En el caso de que el Bluetooth no estuviera activado, lanza una petición al usuario para activarlo*/
    public void requestEnable() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /*Solicitud de hacer visible al dispositivo*/
    public void requestDiscoverable() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isDiscovering()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BT);
        }
    }

    /*Desactivar Bluetooth*/
    public void requestTurnOff() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.disable();
        }
    }


    /*Lista de dispositivos emparejados.
    * Devuelve una lista con los nombres de los dispositivos emparejados, listos para ser mostrados en pantalla
    * Si quisieramos operar con otros datos de cada dispositivo solo hay que devolver un Set de BluetoothDevice
    **/
    public List<String> pairedDevices() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        List<String> list = new ArrayList<String>();
        for (BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());
        return list;
    }

    /*Conexion con el servidor*/
    String device = "192.168.0.10";
    public void startConnection(String device) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice btDevice = adapter.getRemoteDevice(serverAddress);
        connect(btDevice);
    }

    public BluetoothSocket connect(BluetoothDevice device) {
        try{
            btSocket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP_UUID);
            Method m = null;
            try{
                m = device.getClass().getMethod("createInsecureRfcommSocket",
                        new Class[] { int.class });
            }catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }catch (SecurityException e1) {
                e1.printStackTrace();
            }
            try{
                btSocket = (BluetoothSocket) m.invoke(device, 1);
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }catch (InvocationTargetException e){
                e.printStackTrace();
            }
            // Cancel discovery will prevent from errors
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        }catch (IOException e){
            e.printStackTrace();
        }

        // Establish the connection. This will block the app until it connects.
        try{
            btSocket.connect();
            System.out
                    .println("\n...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                System.out
                        .println("Fatal Error. Unable to close socket during connection failure"
                                + e2.getMessage() + ".");
            }
        }

        System.out
                .println("\n...Connection established and data link opened...");

        return btSocket;
    }

    /*Envio de datos*/
    public void send() {
        try {
            outStream = new DataOutputStream(btSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Fatal Error. Output stream creation failed.  "
                    + e.getMessage());
        }

        try {
            outStream.writeFloat(666);
            outStream.writeFloat(accelerometerY);
            outStream.writeFloat(accelerometerZ);
        } catch (IOException e) {
            System.out.println("Check that the SPP UUID: "
                    + BLUETOOTH_SPP_UUID.toString() + " exists on server.\n\n");
        }
    }
}