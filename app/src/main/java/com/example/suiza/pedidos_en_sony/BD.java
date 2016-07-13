package com.example.suiza.pedidos_en_sony;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class BD extends SQLiteOpenHelper {
    public static String NAME = "BDpedidos";           //Nombre Base de Datos de Proyecto
    public static int VERSION = 1;                          //Version (obligatorio)
    public static SQLiteDatabase.CursorFactory CURSORFACTORY = null;        //CursorFactory (obligatorio, siempre null)



 /******Constructor de esta clase******/
    public BD(Context context, String NAME, SQLiteDatabase.CursorFactory CURSORFACTORY, int VERSION) {
        super(context, NAME, CURSORFACTORY, BD.VERSION);
    }

 /*************************************************************************************************/


 /*** Cargado y Reseteado de Tablas ***/

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Clientes(id INTEGER PRIMARY KEY, cod_Cliente INTEGER, razonSocial TEXT, nombreFantasia TEXT, cod_Vendedor INTEGER, direccion TEXT, telefono TEXT, zona INTEGER)");
        db.execSQL("CREATE TABLE Productos(id INTEGER PRIMARY KEY, cod_Producto INTEGER, nombre TEXT, precio INTEGER, disponible TEXT)");
        db.execSQL("CREATE TABLE Vendedor(id INTEGER PRIMARY KEY, nombre TEXT, telefono INTEGER, descripcion TEXT)");    //es el viajante

        /*
        db.execSQL("CREATE TABLE Pedidos(id INTEGER PRIMARY KEY, id_cliente INTEGER, FOREIGN KEY(id_cliente) REFERENCES Clientes(id)," +
                " id_vendedor INTEGER, FOREIGN KEY (id_vendedor) REFERENCES Vendedor(id), cantidadTotal INTEGER, precioTotal INTEGER)");


        db.execSQL("CREATE TABLE Producto_Pedido(id INTEGER PRIMARY KEY, id_producto INTEGER, FOREIGN KEY(id_producto) REFERENCES Productos(id)," +
                        " id_pedido INTEGER, FOREIGN KEY (id_pedido) REFERENCES Pedidos(id), cantidad INTEGER, subtotal INTEGER)");
                        */

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL("DROP TABLE IF EXISTS Clientes");
        db.execSQL("DROP TABLE IF EXISTS Productos");

        db.execSQL("CREATE TABLE Clientes(id INTEGER PRIMARY KEY, cod_Cliente INTEGER, razonSocial TEXT, nombreFantasia TEXT, cod_Vendedor INTEGER, direccion TEXT, telefono TEXT, zona INTEGER)");
        db.execSQL("CREATE TABLE Productos(id INTEGER PRIMARY KEY, cod_Producto INTEGER, nombre TEXT, precio_uni REAL, disponible TEXT)");*/
       // db.execSQL("CREATE TABLE Productos_Pedidos(id INTEGER PRIMARY KEY AUTOINCREMENT," +
              //              "FOREIGN KEY(id_producto) REFERENCES Productos(id), cantidad INTEGER, subtotalProducto REAL)");

        //db.execSQL(productos.CrearTabla);
    }

    public void onDelete(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS Clientes");
        db.execSQL("DROP TABLE IF EXISTS Productos");
        //db.execSQL("DROP TABLE IF EXISTS Viajante");
        //db.execSQL("DROP TABLE IF EXISTS Pedidos");
        //db.execSQL("DROP TABLE IF EXISTS Producto_Pedido");
    }
}
