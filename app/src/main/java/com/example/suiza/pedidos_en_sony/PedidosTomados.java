package com.example.suiza.pedidos_en_sony;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

public class PedidosTomados extends AppCompatActivity {
    TableLayout tabla_productos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_tomados);
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();

        admin.dropearCLIENTESyPRODUCTOS(db);     //Dropear todas las bases de datos
        admin.crearCLIENTESyPRODUCTOS(db);     //Las vuelve a crear
        db.close();
        syncClientesconTxt();
        syncProductosconTxt();

        rellenarTablaPedidosconTxt();

    }

    /****** Lee el txt fuente y devuelve su contenido separado por los "enter"  **********/
    private String[] leerArchivoPedidosSD() throws FileNotFoundException {
        //Encuentra el directorio de la Memoria Externa usando la API
        File ruta_sd = Environment.getExternalStorageDirectory();
        File archivo = new File(ruta_sd.getAbsolutePath() + "/Android/data/LaAutentica/pedidos", "pedidos.txt");
        ByteArrayOutputStream arrayStrings = new ByteArrayOutputStream();  //convierte los bytes del recurso abierto a un array de Strings
        InputStream targetStream = new FileInputStream(archivo);
        try {
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String line;

            if (br.readLine() == null) {
                Toast.makeText(this, "Todavia no se grabaron pedidos en el teléfono", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PedidosTomados.this, MainActivity.class);
                startActivity(intent);
            }


            int i = targetStream.read();
            if (br.readLine() == null) {
                Toast.makeText(this, "Todavia no se grabaron pedidos en el teléfono", Toast.LENGTH_SHORT).show();

            }

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
    private void rellenarTablaPedidosconTxt() {
        TablaClientes tablaclientes = new TablaClientes(this, (TableLayout) findViewById(R.id.PedidosTableLayout));
        tablaclientes.agregarCabecera(R.array.cabecera_tabla_pedidos);


        String[] texto = new String[0];           //"texto" tendra un array de strings donde cada renglon es una celda
        try {
            texto = leerArchivoPedidosSD();
            if (texto.length == 0 || texto.length ==1) {
                Toast.makeText(this, "Todavia no se grabaron pedidos en el teléfono", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(PedidosTomados.this, MainActivity.class);
                startActivity(intent);

            } else {
                for (int i = 0; i < texto.length; i++) {
                    String[] linea = texto[i].split(",");
                    ArrayList<String> elementos = new ArrayList<String>();
                    elementos.add(linea[0]);    // nro boleta
                    elementos.add(linea[16]);    // condicion (Venta,Cambio,Promocion)
                    elementos.add(getNombreCliente(linea[5].trim()).trim());   //cliente   (trim limpia los espacios blancos a los costados para que coincida con los datos en BD)
                    elementos.add(getNombreArticulo(linea[6].trim()).trim());    // articulo
                    elementos.add(linea[7]);    // cantidad del articulo
                    elementos.add(linea[8]);    // subtotal de ese articulo en el pedido
                    elementos.add(linea[1]);    // Fecha del pedido
                    elementos.add(linea[2]);    // Hora del pedido

                    tablaclientes.agregarFilaTabla(elementos);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public String getNombreCliente(String codigo) {
        String nombre = "no se encontro";
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        String consulta = "SELECT razonSocial FROM Clientes WHERE cod_Cliente LIKE '"+codigo+"%'";          //busca: _codigo_  hay: codigo

        Cursor fila = bd.rawQuery(consulta, null);
        if (fila.moveToFirst()) {
            nombre = fila.getString(0);
        }
        return nombre;
    }
    public String getNombreArticulo(String codigo) {
        String nombre="no se encontro";
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        String consulta = "SELECT nombre FROM Productos WHERE cod_Producto LIKE '%"+codigo+"%'";
        Cursor fila = bd.rawQuery(consulta, null);
        if (fila.moveToFirst()) {
            nombre = fila.getString(0);
        } else {
            Toast.makeText(this, "Ocurrio un error al recorrer la tabla Productos", Toast.LENGTH_SHORT).show();
        }
        return nombre;
    }



























    /****** Lee el txt fuente y devuelve su contenido separado por los "enter"  **********/
    private String[] leerArchivoClientesSD() throws FileNotFoundException {
        //Encuentra el directorio de la Memoria Externa usando la API
        File ruta_sd = Environment.getExternalStorageDirectory();
        //Log.i("Ruta de cli"+()+".txt!!", ruta_sd.getAbsolutePath() + "/Android/data/LaAutentica/resources");
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
    private void syncClientesconTxt() {
        //if (cantidadRegistrosClientes() == 0) {         //SI la tabla "Clientes" tiene algun registro, dropear la tabla
        String[] texto = new String[0];           //"texto" tendra un array de strings donde cada renglon es una celda
        try {
            texto = leerArchivoClientesSD();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        admin.dropearCLIENTESyPRODUCTOS(db);     //Dropear todas las bases de datos
        admin.crearCLIENTESyPRODUCTOS(db);     //Las vuelve a crear
        db.beginTransaction();
        for (int i = 0; i < texto.length; i++) {
            String[] linea = texto[i].split(";");
            ContentValues valoresContenidos = new ContentValues();
            valoresContenidos.put("cod_Cliente", linea[0]);
            valoresContenidos.put("razonSocial", linea[1]);
            valoresContenidos.put("nombreFantasia", linea[2]);
            valoresContenidos.put("cuit", linea[3]);
            valoresContenidos.put("direccion", linea[4]);
            valoresContenidos.put("localidad", linea[5]);
            valoresContenidos.put("telefono", linea[6]);
            valoresContenidos.put("cod_Vendedor", linea[7]);
            valoresContenidos.put("vendedor", linea[8]);
            valoresContenidos.put("cod_zona", linea[9]);
            valoresContenidos.put("zona", linea[10]);
            valoresContenidos.put("latitud", linea[11]);
            valoresContenidos.put("longitud", linea[12]);
            db.insert("Clientes", null, valoresContenidos);
        }
        //Toast.makeText(this, "Clientes: " + texto.length, Toast.LENGTH_SHORT).show();
        db.setTransactionSuccessful();
        db.endTransaction();
        //} else {
        //  Toast.makeText(Pedidos.this, "La tabla Clientes ya estaba OK!", Toast.LENGTH_SHORT).show();
        //}
    }

    private void syncProductosconTxt() {
        //if (cantidadRegistrosProductos() == 0) {      //SI la tabla "Clientes" tiene algun registro, dropear la tabla
        String[] texto = new String[0];         //"texto" tendra un array de strings donde cada renglon es una celda
        try {
            texto = leerArchivoProductosSD();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "Leidos:"+texto, Toast.LENGTH_LONG).show();

        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.beginTransaction();
        for (int i = 0; i < texto.length; i++) {
            String[] linea = texto[i].split(";");
            ContentValues valoresContenidos = new ContentValues();
            valoresContenidos.put("cod_Producto", linea[0]);
            valoresContenidos.put("nombre", linea[1]);
            valoresContenidos.put("precio", linea[2]);
            valoresContenidos.put("cantidadMinimaVenta", linea[3]);
            db.insert("Productos", null, valoresContenidos);
        }
        Toast.makeText(this, "Productos disponibles: " + texto.length, Toast.LENGTH_SHORT).show();
        db.setTransactionSuccessful();
        db.endTransaction();
        /*} else {
            Toast.makeText(this, "La tabla PRODUCTOS ya se encontraba sincronizadas al los '.txt'", Toast.LENGTH_LONG).show();
        }*/
    }

    private String[] leerArchivoProductosSD() throws FileNotFoundException {
        File ruta_sd = Environment.getExternalStorageDirectory();
        Log.i("Ruta de ARTPRE.txt!!", ruta_sd.getAbsolutePath() + "/Android/data/LaAutentica/resources");
        File archivo = new File(ruta_sd.getAbsolutePath() + "/Android/data/LaAutentica/resources", "ARTPRE.txt");
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

}
