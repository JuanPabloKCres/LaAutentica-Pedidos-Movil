package com.example.suiza.pedidos_en_sony;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class InfoClientes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_clientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rellenarTablaClientesconTxt();

    }

    /****** Lee el txt fuente y devuelve su contenido separado por los "enter"  **********/
    private String[] leerArchivoClientesSD() throws FileNotFoundException {
        //Encuentra el directorio de la Memoria Externa usando la API
        File ruta_sd = Environment.getExternalStorageDirectory();
        File archivo = new File(ruta_sd.getAbsolutePath() + "/Android/data/LaAutentica/resources", "clientes.txt");
        ByteArrayOutputStream arrayStrings = new ByteArrayOutputStream();  //convierte los bytes del recurso abierto a un array de Strings
        InputStream targetStream = new FileInputStream(archivo);
        try {
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String line;
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

    /**************** Meter el txt en una tabla de BD    ********************/
    private void rellenarTablaClientesconTxt() {
        TablaClientes tablaclientes = new TablaClientes(this, (TableLayout) findViewById(R.id.ClientesTableLayout));
        tablaclientes.agregarCabecera(R.array.cabecera_tabla);


        String[] texto = new String[0];           //"texto" tendra un array de strings donde cada renglon es una celda
        try {
            texto = leerArchivoClientesSD();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < texto.length; i++) {
            String[] linea = texto[i].split(";");
            ArrayList<String> elementos = new ArrayList<String>();
                /*cargando fila*/
            elementos.add(linea[1]);    // razon social
            elementos.add(linea[4]);    // direccion
            elementos.add(linea[5]);    // localidad
            tablaclientes.agregarFilaTabla(elementos);
        }
    }
}
