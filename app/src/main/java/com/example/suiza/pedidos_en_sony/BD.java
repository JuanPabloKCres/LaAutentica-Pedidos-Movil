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
        db.execSQL("CREATE TABLE NumeroPedido(id INTEGER PRIMARY KEY, numero INTEGER DEFAULT 1)");                                //numero de pedido actual
        db.execSQL("CREATE TABLE Clientes(id INTEGER PRIMARY KEY, cod_Cliente TEXT, razonSocial TEXT, nombreFantasia TEXT, cuit TEXT, cod_Vendedor TEXT, vendedor TEXT, direccion TEXT, localidad TEXT, telefono TEXT, cod_zona TEXT, zona TEXT, latitud TEXT, longitud TEXT)");
        db.execSQL("CREATE TABLE Productos(id INTEGER PRIMARY KEY, cod_Producto TEXT, nombre TEXT, precio TEXT, cantidadMinimaVenta INTEGER)");
        db.execSQL("CREATE TABLE Vendedores(id INTEGER PRIMARY KEY, cod_vendedor TEXT, nombre TEXT, user TEXT, password TEXT)");    //es el que toma los pedidos

        db.execSQL("INSERT INTO NumeroPedido (id, numero) Values(1,1)");
        db.execSQL("CREATE TABLE Pedidos(id INTEGER PRIMARY KEY, cod_Pedido TEXT, cod_Cliente TEXT, total INTEGER, fecha TEXT, hora TEXT, cod_Vendedor TEXT, cod_zona TEXT)");

        db.execSQL("CREATE TABLE pedidos_pendientes (id INTEGER PRIMARY KEY, cod_pedido TEXT, cod_cliente TEXT, fecha TEXT, hora TEXT," +
                "cod_Vendedor TEXT, cod_zona TEXT,"+
                "cod_producto TEXT, cantidad TEXT, precio_unitario TEXT, porcentaje_descuento TEXT, importe_descuento_unidad TEXT, precio_unitarioCdescuento TEXT," +
                "subtotal TEXT, latitud TEXT, longitud TEXT, condicion TEXT)");
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

    public void dropearCLIENTESyPRODUCTOS(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS Clientes");
        db.execSQL("DROP TABLE IF EXISTS Productos");
    }

    public void dropearPedidosPendientes(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS pedidos_pendientes");
    }

    public void dropearVendedores(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS Vendedores");
    }
    public void crearVendedores(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Vendedores(id INTEGER PRIMARY KEY, cod_vendedor TEXT, nombre TEXT, user TEXT, password TEXT)");    //es el que toma los pedidos
    }

    public void crearCLIENTESyPRODUCTOS(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Clientes(id INTEGER PRIMARY KEY, cod_Cliente TEXT, razonSocial TEXT, nombreFantasia TEXT, cuit TEXT, cod_Vendedor TEXT, vendedor TEXT, direccion TEXT, localidad TEXT, telefono TEXT, cod_zona TEXT, zona TEXT, latitud TEXT, longitud TEXT)");
        db.execSQL("CREATE TABLE Productos(id INTEGER PRIMARY KEY, cod_Producto TEXT, nombre TEXT, precio INTEGER, cantidadMinimaVenta INTEGER)");
    }

    public void crearClientes(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Clientes(id INTEGER PRIMARY KEY, cod_Cliente TEXT, razonSocial TEXT, nombreFantasia TEXT, cuit TEXT, cod_Vendedor TEXT, vendedor TEXT, direccion TEXT, localidad TEXT, telefono TEXT, cod_zona TEXT, zona TEXT, latitud TEXT, longitud TEXT)");
    }
    public void drpoearClientes(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS Clientes");
    }

    public void crearPedidosPendientes(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pedidos_pendientes (id INTEGER PRIMARY KEY, cod_pedido TEXT, cod_cliente TEXT, fecha TEXT, hora TEXT," +
                "cod_Vendedor TEXT, cod_zona TEXT," +
                "cod_producto TEXT, cantidad TEXT, precio_unitario TEXT, porcentaje_descuento TEXT, importe_descuento_unidad TEXT, precio_unitarioCdescuento TEXT," +
                "subtotal TEXT, latitud TEXT, longitud TEXT, condicion TEXT)");
    }

    public void onDelete(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS Clientes");
        db.execSQL("DROP TABLE IF EXISTS Productos");
        db.execSQL("DROP TABLE IF EXISTS Pedidos");
        db.execSQL("DROP TABLE IF EXISTS NumeroPedido");
        db.execSQL("DROP TABLE IF EXISTS Vendedores");

    }

}
