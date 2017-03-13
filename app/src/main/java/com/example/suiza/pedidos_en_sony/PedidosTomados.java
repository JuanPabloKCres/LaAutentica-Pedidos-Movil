package com.example.suiza.pedidos_en_sony;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        //admin.dropearCLIENTESyPRODUCTOS(db);     //Dropear todas las bases de datos
        //admin.crearCLIENTESyPRODUCTOS(db);     //Las vuelve a crear

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
                /*cargando fila*/
                    elementos.add(linea[0]);    // razon social
                    elementos.add(linea[16]);    // condicion (Venta,Cambio,Promocion)
                    elementos.add(linea[5]);    // cliente
                    elementos.add(linea[6]);    // articulo
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
        String nombre = null;
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String consulta = "SELECT razonSocial FROM Clientes WHERE cod_Cliente LIKE '%"+codigo+"%'";
        Cursor fila = bd.rawQuery(consulta, null);
        if (fila.moveToFirst()) {
            nombre = fila.getString(0);
        } else {
            Toast.makeText(this, "Ocurrio un conflicto al recorrer la tabla Clientes!", Toast.LENGTH_SHORT).show();
        }
        bd.close();
        return nombre;
    }
    public String getNombreArticulo(String codigo) {
        String nombre= "null";
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String consulta = "SELECT nombre FROM Productos WHERE cod_Producto = '" + codigo + "'";
        Cursor fila = bd.rawQuery(consulta, null);
        if (fila.moveToFirst()) {
            nombre = fila.getString(0);
        } else {
            Toast.makeText(this, "Ocurrio un error al recorrer la tabla Productos", Toast.LENGTH_SHORT).show();
        }
        bd.close();
        return nombre;
    }

}
