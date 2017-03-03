package com.example.suiza.pedidos_en_sony;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    /*Declaracion de componentes del activiti de inicio (activity_main)*/
    private GoogleMap mMap;
    private Button clientesBtn, productosBtn, nvoPedidoBtn, sincronizarBtn, rutaDelDiaBtn;
    private static String serverAddress = "00:15:83:0C:BF:EB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //para que no gire la pantalla


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.nvoPedidoFB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Usted se encuentra dentro de la zona...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        /*************** Instanciar Componentes de pantala ******************/
        nvoPedidoBtn = (Button) findViewById(R.id.nvoPedidoBtn);
        sincronizarBtn = (Button) findViewById(R.id.sincronizarBtn);
        rutaDelDiaBtn = (Button) findViewById(R.id.rutasDelDiaBtn);
        clientesBtn = (Button) findViewById(R.id.clientesBtn);
        productosBtn = (Button) findViewById(R.id.productosBtn);
        /********************************************************************/

      /* Redireccion al pulsar los botones */
        nvoPedidoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Pedidos.class);
                startActivity(intent);
            }
        });

        rutaDelDiaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , MapsActivity.class);
                startActivity(intent);
            }
        });
        /**************************************/

        sincronizarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regenerarTablaVendedor();
                syncVendedorconTxt();
                Snackbar.make(v, "Listo...los datos del vendedor fueron cargados desde PC", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        clientesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Vera un listado de los clientes a visitar hoy", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //Intent intent = new Intent(MainActivity.this, ClientesDia.class);
                //startActivity(intent);
            }
        });

        productosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Vera un listado de los productos disponibles para preventa hoy", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //Intent intent = new Intent(MainActivity.this, ProductosDia.class);
                //startActivity(intent);
            }
        });

        findViewById(R.id.nvoPedidoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Pedidos.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /*********************** Metodos para Syncronizar Vendedor con TXT
     * **************************/
    private long cantidadRegistrosVendedor() {
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        long registros = DatabaseUtils.queryNumEntries(db, "Vendedores");
        db.close();
        return registros;
    }

    private void regenerarTablaVendedor(){
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        admin.dropearVendedores(db);
        admin.crearVendedores(db);
    }

    private String[] leerArchivoVendedorSD() throws FileNotFoundException {
        //Encuentra el directorio de la Memoria Externa usando la API
        File ruta_sd = Environment.getExternalStorageDirectory();
        File archivo = new File(ruta_sd.getAbsolutePath() + "/Android/data/LaAutentica/resources", "vendedor.txt");
        ByteArrayOutputStream arrayStrings = new ByteArrayOutputStream();  //convierte los bytes del recurso abierto a un array de Strings
        InputStream targetStream = new FileInputStream(archivo);

        try {
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            int i = targetStream.read();
            while (i != -1) {
                arrayStrings.write(i);
                i = targetStream.read();
            }
            targetStream.close();
            br.close();
        } catch (IOException e) {
            Toast.makeText(this, "error: " + e, Toast.LENGTH_LONG);
        }

        return arrayStrings.toString().split("\n");       //"\n" = "enter"
    }

    /*************** Meter el txt en una tabla de BD ********************/
    private void syncVendedorconTxt(){
        String[] texto = new String[0];
        if(cantidadRegistrosVendedor()==0){         //SI la tabla "Vendedor" tiene algun registro, dropear la tabla
            try {
                texto = leerArchivoVendedorSD();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //"texto" tendra un array de strings donde cada renglon es una celda
            BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.beginTransaction();
            for (int i=0; i<texto.length; i++){
                String[] linea = texto[i].split(";");
                ContentValues valoresContenidos = new ContentValues();
                valoresContenidos.put("cod_vendedor",linea[0]);
                valoresContenidos.put("nombre",linea[1]);
                valoresContenidos.put("user",linea[2]); //Porque user tambien sera el codigo de vendedor (por ahora)
                valoresContenidos.put("password",linea[3]);
                db.insert("Vendedores", null, valoresContenidos);
            }
            Toast.makeText(this, "Datos del Vendedor cargados: " + texto.length, Toast.LENGTH_LONG).show();
            db.setTransactionSuccessful();
            db.endTransaction();
        }else{
            Toast.makeText(MainActivity.this, "Los datos del Vendedor ya estaban Sincronizados.", Toast.LENGTH_SHORT).show();
        }
    }

    //Que salga de la activity apretando 2 veces boton atras!
    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;

    @Override
    public void onBackPressed(){
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else {
            Toast.makeText(this, "Vuelve a presionar para salir de la app", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }
}
