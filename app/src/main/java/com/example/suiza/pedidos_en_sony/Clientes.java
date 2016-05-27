package com.example.suiza.pedidos_en_sony;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JUAMPY on 19/08/2015.--editado en 14 de marzo 2016
 */
public class Clientes extends Activity {

    public static String NombreTabla = "Clientes";
    private String DIRECCION = "direccion";
    private String NOMBRE_CLIENTE = "nombre";

    /*************** Aca va toto lo necesario para usar la clase BD *****************/
    BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
    /*******************************************************************************/

    //***************************************Controles************************************

    private TableLayout TablaLayout;
    private EditText ClienteTxt, DireccionTxt;                   //Casillas de texto
    private Spinner ClienteSpinner;
   // private ArrayAdapter<String> AdapAdaptadorClienteSpinner;

    /////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ClienteSpinner = (Spinner)findViewById(R.id.ClienteSpinner);


        //ClienteSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        /****************************************************************************/
        inflarSpinner();
    }

   /**********************************Alta de un Cliente**************************************/
    public void agregar(View v) {
        BD admin = new BD(this, BD.NAME, null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

    //    String cuit = CUITTxt.getText().toString();
        String nombre = ClienteTxt.getText().toString();

        //Aca Creamos un contenedor para el registro que se va a meter en la BD
        ContentValues registroBD = new ContentValues();
       // registroBD.put("cuit", cuit);
        registroBD.put("nombre", nombre);

        //Aca metemos dicho registro en la BD
        int cant = (int) bd.insert("Clientes", null, registroBD);
        bd.close();

        if (cant > 0) {
            Toast.makeText(this, "Se cargo un nuevo cliente", Toast.LENGTH_SHORT).show();
            inflarSpinner();
            blanquear();
        } else {
            Toast.makeText(this, "No se agrego el cliente, puede ser que ya exista :/", Toast.LENGTH_SHORT).show();
        }
    }

   /********************************Baja de un Cliente*****************************************/
    public void borrar(View view) {
        BD admin = new BD(this, BD.NAME, null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

       // String cuit = CUITTxt.getText().toString();
        String nombre = ClienteTxt.getText().toString();

        int cant = bd.delete("clientes", "cuit=" + "", null);
        bd.close();

        if (cant == 1)
            Toast.makeText(this, "Has borado la informacion del cliente con " + nombre, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "No se pudo borrar los datos porque el dni que queres borrar no existe", Toast.LENGTH_SHORT).show();
        blanquear();
    }

    /*******************************Consultar datos Cliente***********************************/





    /********************************* Blanquear campos *******************************/
    private void blanquear() {
        ClienteTxt.setText("");
        DireccionTxt.setText("");
    }

    /********************* Metodo para recorrer la tabla con el Cursor*****************************/
    public List<String> leerTablaClientes(){
        SQLiteDatabase bd = admin.getReadableDatabase();
        List<String> ListaClientes = new ArrayList<String>();

        String selectQuery = "SELECT * FROM "+NombreTabla+"";
        Cursor cursor = bd.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ListaClientes.add(cursor.getString(2));
            }while (cursor.moveToNext());
        }

        cursor.close();
        bd.close();

        return (ListaClientes);
    }
    /****************************************************************************************/
    private void inflarSpinner(){
        List<String> ListaClientes = new ArrayList<String>();
        ListaClientes = leerTablaClientes();
        ArrayAdapter<String> AdaptadorClienteSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListaClientes);

        // Drop down layout style - list view with radio button
        AdaptadorClienteSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ClienteSpinner.setAdapter(AdaptadorClienteSpinner);
    }
}