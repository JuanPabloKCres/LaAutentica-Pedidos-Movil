package com.example.suiza.pedidos_en_sony;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;



public class Pedidos extends AppCompatActivity implements View.OnClickListener, Fragmento.OnFragmentInteractionListener{

    private EditText CantidadTxt;
    private EditText PrecioUnitarioTxt;
    private EditText SubtotalTxt;
    private EditText CantidadExtraTxt;
    private EditText SubtotalExtraTxt;
    private EditText TotalTxt;
    private EditText productoTxt;
    private Spinner ClienteSpinner;
    private Spinner ProductoSpinner;
    private FloatingActionButton agregarProductoFB;
    private Button ConfirmarPedidoBtn;
    public Switch SolicitadoSwitch;

    public LinearLayout LayoutContenedor;
    private ViewGroup layout;


    int contadorDePulsaciones =1;   //contador de productos pedidos
    int total = 0;                  //total acumulado de pedido para ir mostrando en tiempo real
    private List<EditText> editTextListCantidad = new ArrayList<EditText>();

   /*************** Lo necesario para usar la clase BD *****************/
    //BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
   /********************************************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Fragmento fragment = (Fragmento) getFragmentManager().findFragmentById(R.id.c);
        LayoutContenedor = (LinearLayout)findViewById(R.id.contenedor);
        LayoutContenedor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Se esta presionando el layout extra!!!", Toast.LENGTH_SHORT).show();
                Fragmento fragmento = new Fragmento();
                //administrador de fragmentos
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(fragmento).commit();
                return false;
            }
        });
        CantidadTxt = (EditText)findViewById(R.id.CantidadFijoTxt);
        PrecioUnitarioTxt = (EditText)findViewById(R.id.PrecioUnitarioTxt);
        SubtotalExtraTxt = (EditText)findViewById(R.id.SubtotalExtraTxt);
        TotalTxt = (EditText)findViewById(R.id.TotalTxt);

        agregarProductoFB = (FloatingActionButton) findViewById(R.id.agregarProductoFB);
        agregarProductoFB.setOnClickListener(this);

        ScrollView sv = (ScrollView)findViewById(R.id.scrollView);
        ClienteSpinner = (Spinner)findViewById(R.id.ClienteSpinner);
        ProductoSpinner = (Spinner)findViewById(R.id.ProductoSpinner);
        ConfirmarPedidoBtn = (Button)findViewById(R.id.ConfirmarPedidoBtn);
        ConfirmarPedidoBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Se presiono el botonito", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        /************************************************************************************/

/*
        agregarProductoFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CantidadTxt.getText() != null) {
                    contadorDePulsaciones++;
                    Snackbar.make(v, "Se ha a agregado un Producto al pedido, (van" + contadorDePulsaciones + ")", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    int subtotal = inflarLayout();     // ahora es inflarLayout + calcSubtotal
                    total = total + subtotal;
                    //Toast.makeText(Pedidos.this, "El total completo va sumando : $ "+total, Toast.LENGTH_SHORT).show();
                    TotalTxt.setText("$" + total);
                    //Toast.makeText(Pedidos.this, "No se puede agregar un producto sin especificar la cantidad", Toast.LENGTH_SHORT).show();

                }
            }
        });
*/

        ProductoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String producto = ProductoSpinner.getSelectedItem().toString();

                int precio = precioDelProductoElegido(producto);
                //Toast.makeText(Pedidos.this, "El precio devuelto por la funcion es " +precio, Toast.LENGTH_SHORT).show();
                PrecioUnitarioTxt.setText("$ " + precio);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /********* Ni bien se abre el activity, sincroniza BD desde TXT *****************/
        syncVendedorconTxt();                                                               // Sincroniza datos del vendedor (propietario del celular)
        syncClientesconTxt();
        syncProductosconTxt();
        inflarSpinnerClientes();
        inflarSpinnerProductos();
        /********************************************************************************/


        Toast.makeText(Pedidos.this, "Aqui puede registrar un pedido", Toast.LENGTH_SHORT).show();


        }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick (View v) {
        if (CantidadTxt.getText() != null) {
            contadorDePulsaciones++;
            Snackbar.make(v, "Se ha a agregado un Producto al pedido, (van " + contadorDePulsaciones + ")", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            int subtotal = inflarLayout();     // ahora es inflarLayout + calcSubtotal
            total = total + subtotal;
            //Toast.makeText(Pedidos.this, "El total completo va sumando : $ "+total, Toast.LENGTH_SHORT).show();
            TotalTxt.setText("$" + total);
            //Toast.makeText(Pedidos.this, "No se puede agregar un producto sin especificar la cantidad", Toast.LENGTH_SHORT).show();
            //administrador de transacciones
            //FragmentTransaction transaction = fragmentManager.beginTransaction().add(R.id.contenedor, fragmento);
            //creamos un fragment y lo añadimos a la activity

            //transaction.add(R.id.contenedor,fragmento);
            //transaction.commit();

            if (v.getId() == R.id.agregarProductoFB) {
                Toast.makeText(getApplicationContext(), "Aparentemente funka...un poquillo", Toast.LENGTH_SHORT).show();
                Fragmento fragmento = new Fragmento();
                //administrador de fragmentos
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.contenedor, fragmento).commit();
                //

               // fragmento.SubTotalTxt = (EditText)v.findViewById(R.id.SubtotalExtraTxt);
               // fragmento.SubTotalTxt.setText("$ " + subtotal);

            } else
                Toast.makeText(getApplicationContext(), "Ni se entero que aprete el boton", Toast.LENGTH_SHORT).show();
        }
    }






                    /******************* Inflar Layout con los extra *********************/
    public int inflarLayout() {

        //Componentes del Layout Hijo
        //EditText productoTxt = (EditText)hijo.findViewById(R.id.productoTxt);
        //EditText CantidadExtraTxt = (EditText)hijo.findViewById(R.id.CantidadExtraTxt);
        //EditText SubTotalTxt = (EditText)hijo.findViewById(R.id.SubtotalExtraTxt);


        //editTextListCantidad.add(CantidadExtraTxt);

       ////////////////////////////////////// parte de calcSubtotal /////////////////////////////////////////
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String producto;
        int cantidad=0, precio=0, subtotal = 0, total = 0;

        cantidad = Integer.parseInt(CantidadTxt.getText().toString());      //forzar al EditText a convertir a entero la cantidad
        producto = ProductoSpinner.getSelectedItem().toString();            //conseguimos lo que esta seleccionado en el ProductoSpinner


        Cursor fila = bd.rawQuery("SELECT * FROM Productos WHERE nombre= '" + producto+"'", null);
        if (fila.moveToFirst()) {
            precio = fila.getInt(3);
            Toast.makeText(this, "El precio del producto es $"+precio, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ocurrio un error al calcular el subtotal :(", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "El precio leido es $" + precio, Toast.LENGTH_SHORT).show();
        subtotal = (precio * cantidad);
        Toast.makeText(this, "El subtotal es $" + subtotal, Toast.LENGTH_SHORT).show();

        //SubTotalTxt.setText("$ " + subtotal);



      //  productoTxt.setText(producto);
      //  CantidadExtraTxt.setText(CantidadTxt.getText());
        bd.close();
        return subtotal;
    }



    /***************** Metodo para recorrer la tabla CLientes con el Cursor *******************/
    public List<String> leerTablaClientes(){
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();

        List<String> ListaClientes = new ArrayList<String>();

        String selectQuery = "SELECT * FROM Clientes";
        Cursor cursor = bd.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ListaClientes.add(cursor.getString(2));
                //Toast.makeText(Pedidos.this, "Cod.de clientes registrados: "+cursor.getString(1), Toast.LENGTH_SHORT).show();
            }while (cursor.moveToNext());
        }
        cursor.close();
        bd.close();

        return (ListaClientes);
    }

    /***************** Metodo para recorrer la tabla Productos con el Cursor *******************/
    public List<String> leerTablaProductos(){
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();

        List<String> ListaProductos = new ArrayList<String>();

        String selectQuery = "SELECT * FROM Productos";
        Cursor cursor = bd.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ListaProductos.add(cursor.getString(2));
                //Toast.makeText(Pedidos.this, "Cod.de productos registrados: "+cursor.getString(1), Toast.LENGTH_SHORT).show();
            }while (cursor.moveToNext());
        }
        cursor.close();
        bd.close();

        return (ListaProductos);
    }

    /****************** Inflar Spinner's desde Tablas en BD **********************/
    private void inflarSpinnerClientes(){
        List<String> ListaClientes = new ArrayList<String>();
        ListaClientes = leerTablaClientes();
        ArrayAdapter<String> AdaptadorClienteSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListaClientes);

        // Drop down layout style - list view with radio button
        AdaptadorClienteSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ClienteSpinner.setAdapter(AdaptadorClienteSpinner);
    }

    private void inflarSpinnerProductos(){
        List<String> ListaProductos = new ArrayList<String>();
        ListaProductos = leerTablaProductos();
        ArrayAdapter<String> AdaptadorProductoSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListaProductos);

        // Drop down layout style - list view with radio button
        AdaptadorProductoSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ProductoSpinner.setAdapter(AdaptadorProductoSpinner);
    }

    /******************************************************************************/
    /******************************************************************************/
    /******************************************************************************/
    /******************************************************************************/





////////////para CLIENTES///////////

    /*********** Conseguir la cantidad de registros (filas) que tiene una tabla ***************/
    private long cantidadRegistrosClientes(){
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        long registros = DatabaseUtils.queryNumEntries(db, "Clientes");
        db.close();
        return registros;
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
        if(cantidadRegistrosClientes()==0){         //SI la tabla "Clientes" tiene algun registro, dropear la tabla
            String[] texto = leerArchivoClientesTxt();           //"texto" tendra un array de strings donde cada renglon es una celda
            BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.beginTransaction();
            for (int i=0; i<texto.length; i++){
                String[] linea = texto[i].split(",");
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
            Toast.makeText(this, "Registros insertados: " + texto.length, Toast.LENGTH_LONG).show();
            db.setTransactionSuccessful();
            db.endTransaction();
        }else{
            Toast.makeText(Pedidos.this, "La tabla Clientes ya estaba OK!", Toast.LENGTH_SHORT).show();
        }
    }

    ////////////para VENDEDOR ///////////

    /*********** Conseguir la cantidad de registros (filas) que tiene una tabla ***************/
    private long cantidadRegistrosVendedor(){
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        long registros = DatabaseUtils.queryNumEntries(db, "Vendedor");
        db.close();
        return registros;
    }

    /********** Lee el txt fuente y devuelve su contenido separado por los "enter" **********/
    private String[] leerArchivoVendedorTxt(){
        InputStream inputStream = getResources().openRawResource(R.raw.vendedor);   //abre el recurso "clientes.txt" en la carpeta raw de R (Resources)
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

    private void syncVendedorconTxt(){
        if(cantidadRegistrosVendedor()==0){         //SI la tabla "Vendedor" tiene algun registro, dropear la tabla
            String[] texto = leerArchivoVendedorTxt();           //"texto" tendra un array de strings donde cada renglon es una celda
            BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.beginTransaction();
            for (int i=0; i<texto.length; i++){
                String[] linea = texto[i].split(";");
                ContentValues valoresContenidos = new ContentValues();
                valoresContenidos.put("nombre",linea[0]);
                valoresContenidos.put("telefono",linea[1]);
                valoresContenidos.put("descripcion", linea[2]);

                db.insert("Vendedor", null, valoresContenidos);
            }
            Toast.makeText(this, "Registros insertados: " + texto.length, Toast.LENGTH_LONG).show();
            db.setTransactionSuccessful();
            db.endTransaction();
        }else{
            Toast.makeText(Pedidos.this, "Los datos del Vendedor ya estaban Sincronizados.", Toast.LENGTH_SHORT).show();
        }
    }


////////////////////////////////////////// para PRODUCTOS ///////////////////////////////////////////////

    /********** Lee el txt fuente y devuelve su contenido separado por los "enter" **********/
    private String[] leerArchivoProductosTxt(){
        InputStream inputStream = getResources().openRawResource(R.raw.productos);   //abre el recurso "clientes.txt" en la carpeta raw de R (Resources)
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

    private void syncProductosconTxt() {
        if (cantidadRegistrosProductos() == 0) {      //SI la tabla "Clientes" tiene algun registro, dropear la tabla
            String[] texto = leerArchivoProductosTxt();         //"texto" tendra un array de strings donde cada renglon es una celda
            BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.beginTransaction();

            for (int i = 0; i < texto.length; i++) {
                String[] linea = texto[i].split(",");
                ContentValues valoresContenidos = new ContentValues();
                valoresContenidos.put("cod_Producto", linea[0]);
                valoresContenidos.put("nombre", linea[1]);
                valoresContenidos.put("precio", linea[2]);
                valoresContenidos.put("disponible", linea[3]);
                db.insert("Productos", null, valoresContenidos);
            }

            Toast.makeText(this, "Registros insertados en PRODUCTOS: " + texto.length, Toast.LENGTH_LONG).show();
            db.setTransactionSuccessful();
            db.endTransaction();
        } else {
            Toast.makeText(this, "La tabla PRODUCTOS ya se encontraba sincronizadas al los '.txt'", Toast.LENGTH_LONG).show();
        }
    }


    /*********** Conseguir la cantidad de filas (registros) que tiene una tabla ***************/

    private long cantidadRegistrosProductos(){       // Devuelve la cantidad de filas que tiene una tabla
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        long filas = DatabaseUtils.queryNumEntries(db, "Productos");
        db.close();
        return filas;

    }

                    /**** Devolver el precio de un producto seleccionado ****/
    private int precioDelProductoElegido(String producto){
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();

        String selectQuery = "SELECT * FROM Productos WHERE nombre = '"+producto+"'";
        Cursor cursor = bd.rawQuery(selectQuery, null);
        int precio=0;
        if (cursor.moveToFirst()) {         //falso si el cursor esta vacio
            precio = cursor.getInt(3);
         // Toast.makeText(Pedidos.this, "El precio leido es " +precio, Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        bd.close();

        //PrecioUnitarioTxt.setText(precio);
        Toast.makeText(Pedidos.this, "El precio leido es " +precio, Toast.LENGTH_SHORT).show();
        return precio;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //por ahora queda vacio
    }
}
