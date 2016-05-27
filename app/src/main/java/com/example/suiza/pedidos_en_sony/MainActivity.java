package com.example.suiza.pedidos_en_sony;

import android.content.ContentValues;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    /*Declaracion de componentes del activiti de inicio (activity_main)*/
    private Button clientesBtn, productosBtn, nvoPedidoBtn, sincronizarBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.nvoPedidoFB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Este boton va a servir para agregar un pedido...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        /*************** Instanciar Componentes de pantala ******************/
        nvoPedidoBtn = (Button) findViewById(R.id.nvoPedidoBtn);
        sincronizarBtn = (Button) findViewById(R.id.nvoPedidoBtn);
        /********************************************************************/

      /* Redireccion al pulsar los botones */
        nvoPedidoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //syncClientesconTxt();
                Intent intent = new Intent(MainActivity.this, Pedidos.class);
                startActivity(intent);
            }
        });
        /**************************************/

        sincronizarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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



  /*********** Conseguir la cantidad de filas (registros) que tiene una tabla ***************/

    private long cantidadRegistrosClientes(){       // Devuelve la cantidad de filas que tiene una tabla
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        long filas = DatabaseUtils.queryNumEntries(db, "Clientes");
        db.close();
        return filas;
    }


    /********** Lee el txt fuente y devuelve su contenido separado por los "enter" **********/
    private String[] leerArchivoClientesTxt(){
        InputStream inputStream = getResources().openRawResource(R.raw.clientes);   //abre el recurso "clientes.txt" en la carpeta raw de R (Resources)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  //convierte los bytes del recurso abierto a un array de Strings
        try{
            int i = inputStream.read();     //i contendra el valor de la posicion leida, sera igual a -1 cuando termine de recorrer el archivo de texto
            while (i != -1){
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return	byteArrayOutputStream.toString().split("\n");       //"\n" = "enter"
    }


    /*************** Meter el txt en una tabla de BD ********************/

    private void syncClientesconTxt(){
        if(cantidadRegistrosClientes()!=0){      //SI la tabla "Clientes" tiene algun registro, dropear la tabla
            BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
            admin.onDelete(admin.getReadableDatabase());
            Toast.makeText(this,"Las tablas ya se encontraban sincronizadas al los '.txt'", Toast.LENGTH_LONG).show();
        }

        String[] texto = leerArchivoClientesTxt();         //"texto" tendra un array de strings donde cada renglon es una celda
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.beginTransaction();

        for (int i=0; i<texto.length; i++){
            String[] linea = texto[i].split(";");
            //Toast.makeText(this, texto.toString(), Toast.LENGTH_LONG).show();

            ContentValues valoresContenidos = new ContentValues();
            valoresContenidos.put("cod_Cliente",linea[0]);
            valoresContenidos.put("razonSocial",linea[1]);
            valoresContenidos.put("nombreFantasia", linea[2]);
            valoresContenidos.put("cod_Vendedor",linea[3]);
            valoresContenidos.put("direccion", linea[4]);
            valoresContenidos.put("telefono",linea[5]);
            valoresContenidos.put("zona", linea[6]);
            db.insert("Clientes", null, valoresContenidos);
        }

        Toast.makeText(this, "Registros insertados!" + texto.length, Toast.LENGTH_LONG).show();
        db.setTransactionSuccessful();
        db.endTransaction();

    }


}
