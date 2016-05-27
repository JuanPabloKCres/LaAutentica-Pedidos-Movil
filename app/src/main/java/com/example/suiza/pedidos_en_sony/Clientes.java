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

    }



    /********************************* Blanquear campos *******************************/
    private void blanquear() {
        ClienteTxt.setText("");
        DireccionTxt.setText("");
    }


}