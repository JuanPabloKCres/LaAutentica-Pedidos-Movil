package com.example.suiza.pedidos_en_sony;


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class
        Pedidos extends AppCompatActivity implements View.OnClickListener {
    private EditText CantidadTxt;
    private EditText PrecioUnitarioTxt;
    private EditText SubtotalTxt;
    private EditText CantidadExtraTxt, SubtotalExtraTxt, TotalParcialTxt;   //Total sin Descuento.
    private EditText TotalTxt;
    private EditText DescuentoProductoTxt;
    private Spinner ClienteSpinner, ProductoSpinner, CondicionVentaSpinner;
    private FloatingActionButton agregarProductoFB;
    private FloatingActionButton actualizarTotalFB;
    private Button ConfirmarPedidoBtn;
    ArrayList<String> ListaProductos = new ArrayList<String>();


    //Listas para acumular los Componentes que tienen los pedidos antes de confirmar
    List<EditText> todosLosEditText = new ArrayList<EditText>();
    List<Switch> todosLosSwitch = new ArrayList<Switch>();
    //

    public LinearLayout LayoutContenedor;
    private ViewGroup layout;

    /** Cantidad Minima de venta de productos (para vender solo multiplos)*/
    int cantidad_minima_de_venta = 1;
    /** */
    int contadorDePulsaciones = 1;   //contador de productos pedidos
    double total = 0;                  //total acumulado de pedido para ir mostrando en tiempo real
    double subtotalQueSeBorra = 0;
    private List<EditText> editTextListCantidad = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //para que no gire la pantalla
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        admin.dropearPedidosPendientes(db);
        admin.crearPedidosPendientes(db);
        db.close();
        LayoutContenedor = (LinearLayout) findViewById(R.id.contenedor);
        LayoutContenedor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplication(), "Se esta presionando el layout extra", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        CantidadTxt = (EditText) findViewById(R.id.CantidadFijoTxt);
        CantidadTxt.setText("0");
        CantidadTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                cantidadNuncaNull();
                if(!CantidadTxt.getText().toString().equals("") && !CantidadTxt.getText().toString().equals("0")  ) {
                    if(cantidad_minima_de_venta>1) {
                        validarCantidadIngresada();
                    }
                }
            }
        });

        CantidadTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CantidadTxt.getText().toString().equals("0")) {
                    CantidadTxt.setText("");
                }
            }
        });


        /** SEARCH-SPINNER*/
        //ProductoSpinner.setTitle("Elegi el producto");
        //ProductoSpinner.setPositiveButton("OK");
        /** */
        PrecioUnitarioTxt = (EditText) findViewById(R.id.PrecioUnitarioTxt);
        PrecioUnitarioTxt.setKeyListener(null);
        DescuentoProductoTxt = (EditText) findViewById(R.id.DescuentoProductoTxt);
        DescuentoProductoTxt.setFilters(new InputFilter[]{new InputFilterMinMax("0","100")});
        DescuentoProductoTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                descuentoNuncaNull();
            }
        });
        DescuentoProductoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DescuentoProductoTxt.getText().toString().equals("0")) {
                    DescuentoProductoTxt.setText("");
                }
            }
        });
                //actualizarTotalFB = (FloatingActionButton) findViewById(R.id.actualizarTotalFB);

        /*
        actualizarTotalFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {ar
                double descuento = Double.parseDouble(String.valueOf(DescuentoProductoTxt.getText()));  // % descuento
                double totalParcial = Double.parseDouble(String.valueOf(TotalParcialTxt.getText()));
                double ahorro;
                double totalFinal = totalParcial;
                if (descuento >= 0 && descuento <= 100) {
                    totalFinal = 0;
                    ahorro = (descuento / 100) * totalParcial;
                    totalFinal = totalParcial - ahorro;
                }
                TotalTxt.setText(String.valueOf(totalFinal));

                Snackbar.make(v, "Se ha actualizado el total con el descuento", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });
        */
                TotalParcialTxt = (EditText) findViewById(R.id.TotalParcialTxt);
                TotalParcialTxt.setKeyListener(null);

                TotalTxt = (EditText) findViewById(R.id.TotalTxt);
                TotalTxt.setKeyListener(null);

                agregarProductoFB = (FloatingActionButton) findViewById(R.id.agregarProductoFB);
                agregarProductoFB.setOnClickListener(this);

                ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
                ClienteSpinner = (Spinner) findViewById(R.id.ClienteSpinner);


                ProductoSpinner = (Spinner) findViewById(R.id.ProductoSpinner);
                ArrayAdapter<String> AdaptadorProductoSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListaProductos);
                ProductoSpinner.setAdapter(AdaptadorProductoSpinner);

                ConfirmarPedidoBtn = (Button) findViewById(R.id.ConfirmarPedidoBtn);
                ConfirmarPedidoBtn.setOnClickListener(this);

                CondicionVentaSpinner = (Spinner) findViewById(R.id.CondicionVentaSpinner);
                CondicionVentaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(Pedidos.this, "Se selecciono: " + CondicionVentaSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        if (CondicionVentaSpinner.getSelectedItem().toString().equals("PROMOCION")) {
                            DescuentoProductoTxt.setText("100");
                            DescuentoProductoTxt.setEnabled(false);
                        } else if (CondicionVentaSpinner.getSelectedItem().toString().equals("CAMBIO")) {
                            DescuentoProductoTxt.setText("100");
                            DescuentoProductoTxt.setEnabled(false);
                        } else if (CondicionVentaSpinner.getSelectedItem().toString().equals("VENTA")) {
                            DescuentoProductoTxt.setText("0");
                            DescuentoProductoTxt.setEnabled(true);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                ConfirmarPedidoBtn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(getApplicationContext(), "Marcia Cussatti - Luis Flores", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

                /************************************************************************************/
                ClienteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String cliente = ClienteSpinner.getSelectedItem().toString();
                        String direccion = direccionDelClienteElegido(cliente);
                        Toast.makeText(getApplicationContext(), "Direccion del cliente: "+direccion, Toast.LENGTH_LONG).show();

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                ProductoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String producto = ProductoSpinner.getSelectedItem().toString();

                        double precio = precioDelProductoElegido(producto);
                        PrecioUnitarioTxt.setText("" + precio);
                        /** conseguir la cantidad de venta minima de ese producto */
                        cantidad_minima_de_venta = getCantidadMinima(producto);
                        if(cantidad_minima_de_venta>1){
                            Toast.makeText(getApplicationContext(), "Solo se venden multiplos de "+cantidad_minima_de_venta, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                /********* Ni bien se abre el activity, sincroniza BD desde TXT *****************/
                syncClientesconTxt();
                syncProductosconTxt();
                inflarSpinnerClientes();
                inflarSpinnerProductos();
                /********************************************************************************/
            }

            public void grabarEnTXTMemoriaSD(View v) {
                /**Verificar si la tarjeta externa esta */
                boolean sdDisponible = false;
                boolean sdAccesoEscritura = false;
                String estado = Environment.getExternalStorageState();  //Comprobamos el estado de la memoria externa (tarjeta SD)

                if (estado.equals(Environment.MEDIA_MOUNTED)) {
                    sdDisponible = true;
                    sdAccesoEscritura = true;
                    //Toast.makeText(getApplicationContext(), "La memoria externa esta OK", Toast.LENGTH_SHORT).show();
                } else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                    sdDisponible = true;
                    sdAccesoEscritura = false;
                    Toast.makeText(getApplicationContext(), "La memoria externa esta en modo solo lectura", Toast.LENGTH_SHORT).show();
                } else {
                    sdDisponible = false;
                    sdAccesoEscritura = false;
                    Toast.makeText(getApplicationContext(), "La memoria externa no existe o es inaccesible :(", Toast.LENGTH_SHORT).show();
                }
    /*Fin verificacion*/
       /*Ecribir en memoria externa*/
                File direccionMemoriaExterna = Environment.getExternalStorageDirectory();
                File f = new File(direccionMemoriaExterna + "/Android/data/LaAutentica/pedidos", "Pedidos.txt");
                double subtotal = Double.parseDouble(String.valueOf(CantidadTxt.getText())) * Double.parseDouble(String.valueOf(PrecioUnitarioTxt.getText()));
                double precio_unitario = Double.parseDouble((String.valueOf(PrecioUnitarioTxt.getText())));
                double totalParcial = Double.parseDouble(String.valueOf(TotalParcialTxt.getText()));
                double descuento = Double.parseDouble(String.valueOf(DescuentoProductoTxt.getText()));
                double totalFinal = Double.parseDouble(String.valueOf(TotalTxt.getText()));
                int i = 0;
                if (f.exists()) {
                    try {
                        //Toast.makeText(this, "El archivo de pedido del dia ya existia, se agrego un nuevo articulo.", Toast.LENGTH_SHORT).show();
                        FileWriter fileWritter = new FileWriter(f.getAbsoluteFile(), true);
                        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                        ////////Sacado de StackOverflow (Componentes Visuales Dinamicos referenciados desde una lista)
                        String[] strings = new String[todosLosEditText.size()];
                        strings[i] = todosLosEditText.get(i).getText().toString();
                        List<String> ListaPedidos;
                        ListaPedidos = cargarLineaPedidosConfirmados();
                        for (int pedidoFila = 0; pedidoFila < ListaPedidos.size(); pedidoFila++) {
                            bufferWritter.write(ListaPedidos.get(pedidoFila) + "\n");
                        }

                        //bufferWritter.write(getNumeroPedido() + " , " + fechaActual() + " , " + horaActual() + " , " + getCodVendendor(ClienteSpinner.getSelectedItem().toString()) + " , " +getCodZona(ClienteSpinner.getSelectedItem().toString())+ " , "+ getCodCliente(ClienteSpinner.getSelectedItem().toString()) + " , " + getCodArticulo(strings[i]) + " , " + Cantidad + " , " + PrecioUnitario + " , " + descuentoAplicado + " , " + montoDescuentoUnidad + " , " + PrecioUnitarioCDescuento+" , "+ subtotalConDescuento + " , " + totalFinal + "," + obtenerCoordenadas() + "," + CondicionVentaSpinner.getSelectedItem().toString()+ "\n");
                        //Toast.makeText(this, "Deberia escribir, entro en el for.", Toast.LENGTH_SHORT).show();
                        i = i + 6;
                        //}
                        //bufferWritter.write("**RESUMEN** Coordenadas: "+obtenerCoordenadas()+", Descuento: %"+descuento + " , Descuento: $" + montoDescuento + ", Total de este pedido: $" + totalFinal + "\n");
                        bufferWritter.close();

                        //Toast.makeText(this, "se agregaron " + contadorDePulsaciones + " articulos al archivo de pedidos :) ", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e("Ficheros", "Error al escribir fichero a memoria externa " + e);
                        Toast.makeText(this, "Error al escribir el fichero", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        OutputStreamWriter archivo = new OutputStreamWriter(new FileOutputStream(f));
                        List<String> ListaPedidos;
                        ListaPedidos = cargarLineaPedidosConfirmados();
                        for (int pedidoFila = 0; pedidoFila < ListaPedidos.size(); pedidoFila++) {
                            archivo.write(ListaPedidos.get(pedidoFila) + "\n");
                        }
                        archivo.flush();
                        archivo.close();
                        Toast.makeText(this, "El fichero fue guardado por primera vez, en: " + direccionMemoriaExterna.getAbsolutePath() + "Android/LaAutentica/pedidos", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e("Ficheros", "Error al escribir fichero a memoria externa");
                        Toast.makeText(this, "Error al escribir el fichero", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(this, "Los datos fueron grabados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                int id = item.getItemId();
                //noinspection SimplifiableIfStatement
                return super.onOptionsItemSelected(item);
            }

            @Override   //Cuando se hace click en algun control (cualquiera) se entra a este metodo que revisa cual se apreto
            public void onClick(View v) {
                if (v.getId() == R.id.agregarProductoFB) {
                    if (CantidadTxt.getText().toString().trim().length() != 0){        // si el largo del string cantidad es mayor a cero (no es null)
                        if (Integer.parseInt(CantidadTxt.getText().toString()) > 0) {       // y si la cantidad es mayor a 0
                            if (Double.parseDouble(PrecioUnitarioTxt.getText().toString()) > 0) {
                                if ((validarCantidadIngresada() == true) || (cantidad_minima_de_venta < 2)) {         /** Validar que sea mayor a la cantidad minima, si es mayor devuelve true */
                                    if (0 <= Integer.parseInt(DescuentoProductoTxt.getText().toString()) && (Integer.parseInt(DescuentoProductoTxt.getText().toString()) <= 100)) {
                                        contadorDePulsaciones++;
                                        Snackbar.make(v, "Se añadio un Articulo al pedido, (" + (contadorDePulsaciones - 1) + ")", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        /****** INICIO inflarLayout programaticamente ******/
                                        final TextView productoExtraLbl;
                                        final TextView CantidadExtraLbl;
                                        final TextView SubtotalExtraLbl;
                                        final TextView SubtotaCDescuentolLbl;
                                        final TextView PrecioUnitLbl;
                                        final TextView DescuentoExtraLbl;
                                        final EditText productoTxtDinamico;
                                        final EditText CantidadExtraTxt;
                                        final EditText SubtotalExtraTxt;
                                        final EditText SubTotalCDescuentoExtraTxt;
                                        final EditText PrecioUnitTxt;
                                        final EditText DescuentoExtraTxt;

                                        final Switch SolicitadoSwitch;

                                        productoExtraLbl = new TextView(Pedidos.this);
                                        PrecioUnitLbl = new TextView(Pedidos.this);
                                        CantidadExtraLbl = new TextView(Pedidos.this);
                                        SubtotalExtraLbl = new TextView(Pedidos.this);
                                        SubtotaCDescuentolLbl = new TextView(Pedidos.this);
                                        DescuentoExtraLbl = new TextView(Pedidos.this);

                                        productoTxtDinamico = new EditText(Pedidos.this);
                                        productoTxtDinamico.setKeyListener(null);
                                        productoTxtDinamico.setTextSize(12);
                                        CantidadExtraTxt = new EditText(Pedidos.this);
                                        CantidadExtraTxt.setKeyListener(null);
                                        CantidadExtraTxt.setTextSize(15);
                                        CantidadExtraTxt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

                                        SubtotalExtraTxt = new EditText(Pedidos.this);
                                        SubtotalExtraTxt.setKeyListener(null);

                                        SubTotalCDescuentoExtraTxt = new EditText(Pedidos.this);
                                        SubTotalCDescuentoExtraTxt.setKeyListener(null);

                                        PrecioUnitTxt = new EditText(Pedidos.this);
                                        PrecioUnitTxt.setTextSize(15);
                                        PrecioUnitTxt.setKeyListener(null);

                                        DescuentoExtraTxt = new EditText(Pedidos.this);
                                        DescuentoExtraTxt.setKeyListener(null);

                                        SolicitadoSwitch = new Switch(Pedidos.this);        //Switch para desactivar un producto del pedido

                                        //el "id" de cada componente sera igual para identificar el pedido al que corresponde
                                        int id = contadorDePulsaciones;
                                        //fijamos los id de los componentes dinamicos
                                        SolicitadoSwitch.setId(id);
                                        SolicitadoSwitch.setChecked(true);
                                        SolicitadoSwitch.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        productoExtraLbl.setId(id);
                                        productoExtraLbl.setText("Producto: ");
                                        productoExtraLbl.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //
                                        PrecioUnitLbl.setId(id);
                                        PrecioUnitLbl.setText("Precio Unitario: $");
                                        PrecioUnitLbl.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //
                                        CantidadExtraLbl.setId(id);
                                        CantidadExtraLbl.setText("Cantidad:");
                                        CantidadExtraLbl.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        SubtotalExtraLbl.setId(id);
                                        SubtotalExtraLbl.setText("Subtotal:");
                                        SubtotalExtraLbl.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //
                                        DescuentoExtraLbl.setId(id);
                                        DescuentoExtraLbl.setText("Descuento a arti (%):");
                                        DescuentoExtraLbl.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //
                                        SubtotaCDescuentolLbl.setId(id);
                                        SubtotaCDescuentolLbl.setText("Subtotal c/ el descuento: $");
                                        SubtotaCDescuentolLbl.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //
                                        productoTxtDinamico.setId(id);
                                        productoTxtDinamico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //
                                        PrecioUnitTxt.setId(id);
                                        PrecioUnitTxt.setText(PrecioUnitarioTxt.getText());
                                        PrecioUnitTxt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //
                                        CantidadExtraTxt.setId(id);
                                        CantidadExtraTxt.setText(CantidadTxt.getText());
                                        CantidadExtraTxt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        SubtotalExtraTxt.setId(id);
                                        SubtotalExtraTxt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //
                                        DescuentoExtraTxt.setId(id);
                                        DescuentoExtraTxt.setText(DescuentoProductoTxt.getText().toString());   //mete en el Descuento ET de un producto el Descuento de cuando se agrego.
                                        DescuentoExtraTxt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //
                                        SubTotalCDescuentoExtraTxt.setId(id);
                                        SubTotalCDescuentoExtraTxt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                        //guardamos los componentes en una lista para accederlos despues
                                        //EditText's
                                        todosLosEditText.add(productoTxtDinamico);  //posicion 0
                                        todosLosEditText.add(CantidadExtraTxt);     //posicion 1
                                        todosLosEditText.add(SubtotalExtraTxt);     //posicion 2
                                        todosLosEditText.add(PrecioUnitTxt);        //posicion 3
                                        //Nota: mas abajo tambien hacemos un add de SubtotalExtra
                                        todosLosEditText.add(DescuentoExtraTxt);            //new!

                                        //Switch's
                                        todosLosSwitch.add(SolicitadoSwitch);

                                        //instancio los componentes, para que aparezcan en la vista
                                        LayoutContenedor.addView(SolicitadoSwitch);
                                        LayoutContenedor.addView(productoExtraLbl);
                                        LayoutContenedor.addView(productoTxtDinamico);
                                        LayoutContenedor.addView(PrecioUnitLbl);
                                        LayoutContenedor.addView(PrecioUnitTxt);
                                        LayoutContenedor.addView(CantidadExtraLbl);
                                        LayoutContenedor.addView(CantidadExtraTxt);
                                        LayoutContenedor.addView(SubtotalExtraLbl);
                                        LayoutContenedor.addView(SubtotalExtraTxt);
                                        LayoutContenedor.addView(SubtotaCDescuentolLbl);    //new!
                                        LayoutContenedor.addView(SubTotalCDescuentoExtraTxt);   //new!
                                        LayoutContenedor.addView(DescuentoExtraLbl);            //new!
                                        LayoutContenedor.addView(DescuentoExtraTxt);            //new!
                                        //FIN generacion dinamica de vista "Detalle Pedido"

                                        ////////////////////////////////////// parte de calcSubtotal /////////////////////////////////////////
                                        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                                        final SQLiteDatabase bd = admin.getWritableDatabase();
                                        String producto;
                                        int cantidad = 0;
                                        double precio = 0, subtotal = 0, subtotalConDescuentoIncluido = 0;

                                        if (CantidadTxt.getText() == null) {
                                            CantidadTxt.setText("0");
                                            cantidad = 0;
                                        } else {
                                            cantidad = Integer.parseInt(CantidadTxt.getText().toString());      //forzar al EditText a convertir a entero la cantidad
                                        }

                                        producto = ProductoSpinner.getSelectedItem().toString();            //conseguimos lo que esta seleccionado en el ProductoSpinner

                                        Cursor fila = bd.rawQuery("SELECT * FROM Productos WHERE nombre= '" + producto + "'", null);
                                        if (fila.moveToFirst()) {
                                            precio = Double.parseDouble(fila.getString(3));
                                            precio = redondear(precio, 3);
                                            //Toast.makeText(this, "El precio del producto es $" + precio, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Ocurrio un error al calcular el subtotal :(", Toast.LENGTH_SHORT).show();
                                        }

                                        subtotal = (precio * cantidad);
                                        subtotal = redondear(subtotal, 3);
                                        SubtotalExtraTxt.setText("" + subtotal);
                                        todosLosEditText.add(SubTotalCDescuentoExtraTxt);   //new!
                                        productoTxtDinamico.setText(producto);
                                        bd.close();

                                        //ArrayAdapter<String> AdaptadorProductoSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListaProductos);
                                        // la magia de calcular el total final considerando el % de descuento y el total parcial
                                        double descuento = Double.parseDouble(String.valueOf(DescuentoProductoTxt.getText()));
                                        descuento = redondear(descuento, 3);
                                        //Log.(Double.parseDouble(String.valueOf(TotalParcialTxt.getText())));
                                        double totalParcial = Double.parseDouble(TotalParcialTxt.getText().toString());
                                        double ahorroTotal, ahorroXproducto;
                                        double totalFinal = totalParcial;

                                        if (descuento >= 0 && descuento <= 100) {
                                            ahorroXproducto = (descuento / 100) * subtotal;
                                            ahorroXproducto = redondear(ahorroXproducto, 3);
                                            subtotalConDescuentoIncluido = subtotal - ahorroXproducto;
                                            subtotalConDescuentoIncluido = redondear(subtotalConDescuentoIncluido, 2);
                                            total = total + subtotalConDescuentoIncluido;
                                            total = redondear(total, 3);
                                        }

                                        /** SWITCH para dar de baja un articulo ****/
                                        SolicitadoSwitch.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                subtotalQueSeBorra = Double.parseDouble(SubTotalCDescuentoExtraTxt.getText().toString());
                                                total = total - subtotalQueSeBorra;
                                                total = redondear(total, 3);
                                                TotalTxt.setText(String.valueOf(total));
                                                SolicitadoSwitch.setChecked(false);
                                                SolicitadoSwitch.setVisibility(View.GONE);
                                                LayoutContenedor.removeView(SolicitadoSwitch);
                                                //LayoutContenedor.removeView(productoExtraLbl);
                                                productoExtraLbl.setVisibility(View.GONE);
                                                //LayoutContenedor.removeView(PrecioUnitLbl);
                                                PrecioUnitLbl.setVisibility(View.GONE);
                                                LayoutContenedor.removeView(productoTxtDinamico);
                                                //LayoutContenedor.removeView(PrecioUnitTxt);
                                                PrecioUnitTxt.setVisibility(View.GONE);
                                                //LayoutContenedor.removeView(CantidadExtraLbl);
                                                CantidadExtraLbl.setVisibility(View.GONE);
                                                //LayoutContenedor.removeView(CantidadExtraTxt);
                                                CantidadExtraTxt.setVisibility(View.GONE);
                                                //LayoutContenedor.removeView(SubtotalExtraLbl);
                                                SubtotalExtraLbl.setVisibility(View.GONE);
                                                LayoutContenedor.removeView(SubtotalExtraTxt);
                                                LayoutContenedor.removeView(SubtotaCDescuentolLbl);    //new!
                                                SubtotaCDescuentolLbl.setVisibility(View.GONE);
                                                //LayoutContenedor.removeView(SubTotalCDescuentoExtraTxt);   //new!
                                                SubTotalCDescuentoExtraTxt.setVisibility(View.GONE);
                                                SubtotalExtraTxt.setVisibility(View.GONE);
                                                //LayoutContenedor.removeView(DescuentoExtraLbl);            //new!
                                                DescuentoExtraLbl.setVisibility(View.GONE);
                                                //LayoutContenedor.removeView(DescuentoExtraTxt);            //new!
                                                DescuentoExtraTxt.setVisibility(View.GONE);
                                                ///////////////////////////////// quitar ese registro de la bd:  ///////////////////////////////////////
                                                try {
                                                    String cod_producto_para_borrar = getCodArticulo(productoTxtDinamico.getText().toString());
                                                    BD admin = new BD(Pedidos.this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                                                    SQLiteDatabase bd = admin.getWritableDatabase();
                                                    String query = "DELETE FROM pedidos_pendientes WHERE cod_producto = '" + cod_producto_para_borrar + "';";
                                                    bd.execSQL(query);
                                                    bd.close();
                                                } catch (Exception e) {
                                                    Toast.makeText(Pedidos.this, "Error al quitar producto del carrito", Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(Pedidos.this, "Codigo error: " + e, Toast.LENGTH_LONG).show();
                                                    Log.e("Al borrar un registro", "Error!:" + e);
                                                }
                                                Toast.makeText(Pedidos.this, "Se resto el subtotal: $ " + subtotalQueSeBorra + " al total, que es " + total, Toast.LENGTH_LONG).show();
                                            }
                                        });


                                        /****** FIN Experimento inflarLayout embebido******/
                                        /**Deshabilitando el ClienteSpinner para que no pueda volver a seleccionarse en el mismo pedido**/
                                        ClienteSpinner.setEnabled(false);

                                        /***Deshabilitando el ProductoSpinner para que no pueda volver a seleccionarse en el mismo pedido ***/
                        /*
                        int posicion = ProductoSpinner.getSelectedItemPosition();
                        if (posicion > -1) {
                            ArrayAdapter<String> AdaptadorProductoSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListaProductos);
                            AdaptadorProductoSpinner.remove(producto);
                            AdaptadorProductoSpinner.notifyDataSetChanged();
                            Toast.makeText(this, "Se quito un articulo del spinner de disponibles", Toast.LENGTH_SHORT).show();
                        }
                        */

                                        TotalTxt.setText(String.valueOf(total));
                                        subtotalQueSeBorra = 0;
                                        SubTotalCDescuentoExtraTxt.setText(String.valueOf(subtotalConDescuentoIncluido));

                                        Snackbar.make(v, "Se añadio un nuevo producto al carrito", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        ////////////////////////////////////////////////////////////////////////////////////////
                                        String nombreCli = ClienteSpinner.getSelectedItem().toString();
                                        guardarPedidoPendiente(nombreCli);
                                        ///////////////////////////////////////////////////////////////////////////////////////
                                        CantidadTxt.setText("0");
                                        PrecioUnitarioTxt.setText("0");
                                        DescuentoProductoTxt.setText("0");
                                    } else {
                                        Toast.makeText(Pedidos.this, "El campo 'Descuento' debe estar entre 0 y 100%", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Pedidos.this, "La cantidad de venta no se admite porque ése producto solo se vende de a " + cantidad_minima_de_venta, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(Pedidos.this,  "No se puede vender con Precio Unitario 0", Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            Toast.makeText(Pedidos.this,"Complete el campo 'Cantidad'", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Pedidos.this, "No se puede agregar un producto sin especificar la cantidad", Toast.LENGTH_SHORT).show();
                    }
                } else if (v.getId() == R.id.ConfirmarPedidoBtn) {
                    if((total>0) && (consultarSiHayProductosEnCarrito()==true)) {
                       //Pantalla de 'cargando...' aqui

                        //
                        /***** mensaje de alerta (confirmar Si/No)  ********
                         AlertDialog.Builder builder = new AlertDialog.Builder(this);
                         builder.setMessage("Vas a confirmar un nuevo pedido, estas seguro?").setPositiveButton("Si", dialogClickListener)
                         .setNegativeButton("No", dialogClickListener).show();
                         ** *********************************************************/
                        grabarEnTXTMemoriaSD(v);
                        actualizarNumeroPedido();
                        /************** dropear la tabla de 'pedidos_pendientes' y crearla devuelta *******/
                        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                        SQLiteDatabase bd = admin.getWritableDatabase();
                        admin.dropearPedidosPendientes(bd);
                        admin.crearPedidosPendientes(bd);
                        /*******************************************************************************/

                        try {
                            // Simulate network access.
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        Intent intent = new Intent(Pedidos.this, Resumen.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(Pedidos.this, "No se puede confirmar un pedido cuyo importe total es '0'", Toast.LENGTH_SHORT).show();
                    }
                }
                ////////////////////////////////////////////////////////////////////////////////////////
                else {
                    Toast.makeText(Pedidos.this, "No se puede agregar un producto sin especificar la cantidad", Toast.LENGTH_SHORT).show();
                }
            }


            public void actualizarNumeroPedido() {       //Incrementa en 1 el numero de pedido actual
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getWritableDatabase();
                int numeroActual = getNumeroPedido();
                int numeroNuevo;
                numeroNuevo = numeroActual + 1;
                String query = "UPDATE NumeroPedido SET numero = " + numeroNuevo + ";";
                bd.execSQL(query);
                bd.close();
            }

            public int getNumeroPedido() {
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getWritableDatabase();
                int numeroActual = -999;

                Cursor fila = bd.rawQuery("SELECT * FROM NumeroPedido", null);
                if (fila.moveToFirst()) {
                    numeroActual = fila.getInt(1);
                } else {
                    Toast.makeText(this, "Ocurrio un error al recorrer la tabla NumeroPedido!", Toast.LENGTH_SHORT).show();
                }
                bd.close();
                return numeroActual;
            }

            public String getCodCliente(String nombre) {
                String codigoCliente = null;
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getWritableDatabase();
                String consulta = "SELECT cod_Cliente FROM Clientes WHERE razonSocial = '" + nombre + "'";
                Cursor fila = bd.rawQuery(consulta, null);
                if (fila.moveToFirst()) {
                    codigoCliente = fila.getString(0);
                    //Toast.makeText(this, "El precio del producto es $"+precio, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Ocurrio un error al recorrer la tabla Clientes!", Toast.LENGTH_SHORT).show();
                }
                bd.close();
                return codigoCliente;
            }

            public String getCodVendendor(String nombre) {
                String codigoVendedor = null;
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getWritableDatabase();
                String consulta = "SELECT cod_Vendedor FROM Clientes WHERE razonSocial = '" + nombre + "'";
                Cursor fila = bd.rawQuery(consulta, null);
                if (fila.moveToFirst()) {
                    codigoVendedor = fila.getString(0);
                } else {
                    Toast.makeText(this, "Ocurrio un error al recorrer la tabla Clientes!", Toast.LENGTH_SHORT).show();
                }
                bd.close();
                return codigoVendedor;
            }

            public String getCodZona(String nombre) {
                String codigoZona = null;
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getWritableDatabase();
                String consulta = "SELECT cod_zona FROM Clientes WHERE razonSocial = '" + nombre + "'";
                Cursor fila = bd.rawQuery(consulta, null);
                if (fila.moveToFirst()) {
                    //codigoZona = fila.getString(9);
                    codigoZona = fila.getString(0);
                } else {
                    Toast.makeText(this, "Ocurrio un error al recorrer la tabla Clientes!", Toast.LENGTH_SHORT).show();
                }
                bd.close();
                return codigoZona;
            }

            public String getCodArticulo(String nombre) {
               // Toast.makeText(this, "El nombre que entra a getCodArtuculo es: " + nombre, Toast.LENGTH_SHORT).show();
                String codigoArticulo = "null";
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getWritableDatabase();
                String consulta = "SELECT cod_Producto FROM Productos WHERE nombre = '" + nombre + "'";
                Cursor fila = bd.rawQuery(consulta, null);
                if (fila.moveToFirst()) {
                    codigoArticulo = fila.getString(0);
                } else {
                    Toast.makeText(this, "Ocurrio un error al recorrer la tabla Productos", Toast.LENGTH_SHORT).show();
                }
                bd.close();
                return codigoArticulo;
            }


            public String fechaActual() {
                String fecha = null;
                Date date = new Date();
                DateFormat formato = new SimpleDateFormat("dd/MM/yy");
                fecha = formato.format(date);
                return fecha;
            }

            public String horaActual() {
                String hora = null;
                Date date = new Date();
                DateFormat formato = new SimpleDateFormat("HH:mm:ss");
                hora = formato.format(date);
                return hora;
            }

            public String fechaActualparaNombredeArchivo() {         //solo mes y dia para no superar los 8 digitos de FoxPro
                String fecha = null;
                Date date = new Date();
                DateFormat formato = new SimpleDateFormat("MMdd");
                fecha = formato.format(date);
                return fecha;
            }

            /*****************
             * Metodo para recorrer la tabla CLientes con el Cursor
             *******************/
            public List<String> leerTablaClientes() {
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getReadableDatabase();

                List<String> ListaClientes = new ArrayList<String>();

                String selectQuery = "SELECT * FROM Clientes";
                Cursor cursor = bd.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        ListaClientes.add(cursor.getString(2));
                        //Toast.makeText(Pedidos.this, "Cod.de clientes registrados: "+cursor.getString(1), Toast.LENGTH_SHORT).show();
                    } while (cursor.moveToNext());
                }
                cursor.close();
                bd.close();
                return (ListaClientes);
            }

            /*****************
             * Metodo para recorrer la tabla Productos con el Cursor
             *******************/
            public ArrayList<String> leerTablaProductos() {
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getReadableDatabase();

                ArrayList<String> ListaProductos = new ArrayList<String>();

                String selectQuery = "SELECT * FROM Productos";
                Cursor cursor = bd.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        ListaProductos.add(cursor.getString(2));
                        //Toast.makeText(Pedidos.this, "Cod.de productos registrados: "+cursor.getString(1), Toast.LENGTH_SHORT).show();
                    } while (cursor.moveToNext());
                }
                cursor.close();
                bd.close();
                return (ListaProductos);
            }

            /****************** Inflar Spinner's desde Tablas en BD **********************/
            private void inflarSpinnerClientes() {
                List<String> ListaClientes = new ArrayList<String>();
                ListaClientes = leerTablaClientes();
                ArrayAdapter<String> AdaptadorClienteSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListaClientes);

                // Drop down layout style - list view with radio button
                AdaptadorClienteSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                ClienteSpinner.setAdapter(AdaptadorClienteSpinner);
            }

            private void inflarSpinnerProductos() {
                ListaProductos = leerTablaProductos();
                // Drop down layout style - list view with radio button
                ArrayAdapter<String> AdaptadorProductoSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListaProductos);
                AdaptadorProductoSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner

                ProductoSpinner.setAdapter(AdaptadorProductoSpinner);
            }

            /******************************************************************************/
            /******************************************************************************/

            /***********
             * Conseguir la cantidad de registros (filas) que tiene una tabla
             ***************/
            private long cantidadRegistrosClientes() {
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase db = admin.getReadableDatabase();
                long registros = DatabaseUtils.queryNumEntries(db, "Clientes");
                db.close();
                return registros;
            }

            /**********
             * Lee el txt fuente y devuelve su contenido separado por los "enter"
             **********/
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
                    //int i = 0;
            /*
            while ((line = br.readLine()) != null) {
                arrayStrings.write(i);
                i++;
            }
            */
                    br.close();
                } catch (IOException e) {
                    Toast.makeText(this, "error: " + e, Toast.LENGTH_LONG);
                }
                return arrayStrings.toString().split("\n");       //"\n" = "enter"
            }

            /***************
             * Meter el txt en una tabla de BD
             ********************/
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
                Toast.makeText(this, "Clientes: " + texto.length, Toast.LENGTH_SHORT).show();
                db.setTransactionSuccessful();
                db.endTransaction();
                //} else {
                //  Toast.makeText(Pedidos.this, "La tabla Clientes ya estaba OK!", Toast.LENGTH_SHORT).show();
                //}
            }
////////////////////////////////////////// para PRODUCTOS ///////////////////////////////////////////////

            /**********
             * Lee el txt fuente y devuelve su contenido separado por los "enter"
             **********/
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

            /***************
             * Meter el txt en una tabla de BD
             ********************/

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

            /***********
             * Conseguir la cantidad de filas (registros) que tiene una tabla
             ***************/
            private long cantidadRegistrosProductos() {       // Devuelve la cantidad de filas que tiene una tabla
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase db = admin.getReadableDatabase();
                long filas = DatabaseUtils.queryNumEntries(db, "Productos");
                db.close();
                return filas;
            }

            /****
             * Devolver el precio de un producto seleccionado
             ****/
            private double precioDelProductoElegido(String producto) {
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getReadableDatabase();
                String selectQuery = "SELECT * FROM Productos WHERE nombre = '" + producto + "'";
                Cursor cursor = bd.rawQuery(selectQuery, null);
                double precio = 0;
                if (cursor.moveToFirst()) {         //falso si el cursor esta vacio
                    precio = Double.parseDouble(cursor.getString(3));
                }

                cursor.close();
                bd.close();
                return precio;
            }



            public String idVendedorTxt() {
                String codigoVendedor = null;
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getWritableDatabase();
                String consulta = "SELECT cod_vendedor FROM Vendedores";
                Cursor fila = bd.rawQuery(consulta, null);
                if (fila.moveToFirst()) {
                    codigoVendedor = fila.getString(0);
                } else {
                    Toast.makeText(this, "Ocurrio un error al recorrer la tabla Vendedores", Toast.LENGTH_SHORT).show();
                }
                bd.close();
                return codigoVendedor;
            }

            public static double redondear(double value, int places) {      //deja un Double en dos digitos despues de coma (xxxxxx.##)
                if (places < 0) throw new IllegalArgumentException();

                BigDecimal bd = new BigDecimal(value);
                bd = bd.setScale(places, RoundingMode.HALF_EVEN);
                return bd.doubleValue();
            }

            //StackOverflow. Conseguir latitud y longitud
            public String obtenerCoordenadas() {
                Context contexto = this;
                String coordenadas = "No se pudo acceder al GPS";
                if (ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Pedidos.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                } else {
                    //Toast.makeText(contexto,"Necesitas otorgar permisos para usar el GPS, hable con el Sysadmin",Toast.LENGTH_SHORT).show();
                    GPSTracker gps = new GPSTracker(contexto, Pedidos.this);

                    // Revisar si GPS esta activado
                    if (gps.canGetLocation()) {
                        double latitud = gps.getLatitude();
                        double longitud = gps.getLongitude();
                        coordenadas = String.valueOf(latitud) + " , " + String.valueOf(longitud);

                        // \n is for new line
                        //Toast.makeText(getApplicationContext(), "El pedido se tomo en - \nLat: " + latitud + "\nLong: " + longitud, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }
                }
                return coordenadas;
            }

            public String getLatitud() {
                Context contexto = this;
                String latitud = "No se pudo acceder al GPS";
                if (ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Pedidos.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    //Toast.makeText(contexto,"Necesitas otorgar permisos para usar el GPS, hable con el Sysadmin",Toast.LENGTH_SHORT).show();
                    GPSTracker gps = new GPSTracker(contexto, Pedidos.this);
                    if (gps.canGetLocation()) {     // Revisar si GPS esta activado
                        double lat = gps.getLatitude();
                        latitud = String.valueOf(lat);
                    } else {
                        gps.showSettingsAlert();
                    }
                }
                return latitud;
            }

            public String getLongitud() {
                Context contexto = this;
                String longitud = "No se pudo acceder al GPS";
                if (ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Pedidos.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    //Toast.makeText(contexto,"Necesitas otorgar permisos para usar el GPS, hable con el Sysadmin",Toast.LENGTH_SHORT).show();
                    GPSTracker gps = new GPSTracker(contexto, Pedidos.this);
                    // Revisar si GPS esta activado
                    if (gps.canGetLocation()) {
                        double lng = gps.getLongitude();
                        longitud = String.valueOf(lng);
                    } else {
                        gps.showSettingsAlert();
                    }
                }
                return longitud;
            }

            /** Que salga de la activity apretando 2 veces boton atras y que dropee tabla 'pedidos_pendientes'     **/
            private static final int INTERVALO = 2000; //2 segundos para salir
            private long tiempoPrimerClick;
            @Override
            public void onBackPressed() {
                if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()) {
                    super.onBackPressed();
                    BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                    SQLiteDatabase db = admin.getWritableDatabase();
                    admin.dropearPedidosPendientes(db);
                    admin.crearPedidosPendientes(db);
                    db.close();
                    return;
                } else {
                    Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
                }
                tiempoPrimerClick = System.currentTimeMillis();
            }

            /***********************************************************************/

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Toast.makeText(context.this, "Se añadio un articulo al carrito", Toast.LENGTH_SHORT).show();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            //guarda el pedido en la tabla 'pedidos_pendientes'
            public void guardarPedidoPendiente(String nombreCli) {
                ContentValues valoresContenidos = new ContentValues();
                valoresContenidos.put("cod_pedido", String.valueOf(getNumeroPedido()));
                valoresContenidos.put("cod_cliente", getCodCliente(nombreCli));
                valoresContenidos.put("fecha", fechaActual());
                valoresContenidos.put("hora", horaActual());
                valoresContenidos.put("cod_Vendedor", getCodVendendor(nombreCli));
                valoresContenidos.put("cod_zona", getCodZona(nombreCli));
                valoresContenidos.put("cod_producto", getCodArticulo(ProductoSpinner.getSelectedItem().toString()));
                valoresContenidos.put("cantidad", CantidadTxt.getText().toString());
                valoresContenidos.put("precio_unitario", PrecioUnitarioTxt.getText().toString());
                valoresContenidos.put("porcentaje_descuento", DescuentoProductoTxt.getText().toString());
                double precioUnitario = Double.parseDouble(PrecioUnitarioTxt.getText().toString());
                double porcentajeDescuento = Double.parseDouble(DescuentoProductoTxt.getText().toString());
                double montoDescuentoUnidad = precioUnitario * (porcentajeDescuento / 100);
                valoresContenidos.put("importe_descuento_unidad", montoDescuentoUnidad);
                double pUnitarioCDescuento = precioUnitario - montoDescuentoUnidad;
                valoresContenidos.put("precio_unitarioCdescuento", pUnitarioCDescuento);
                double subtotalConDescuento = (Double.parseDouble(CantidadTxt.getText().toString())) * pUnitarioCDescuento;
                valoresContenidos.put("subtotal", subtotalConDescuento);
                valoresContenidos.put("latitud", getLatitud());
                valoresContenidos.put("longitud", getLongitud());
                valoresContenidos.put("condicion", CondicionVentaSpinner.getSelectedItem().toString());

                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase db = admin.getWritableDatabase();
                db.beginTransaction();
                db.insert("pedidos_pendientes", null, valoresContenidos);
                //Toast.makeText(this, "Se cargo el producto a la lista de pendientes..", Toast.LENGTH_LONG).show();
                db.setTransactionSuccessful();
                db.endTransaction();
            }

            private List<String> cargarLineaPedidosConfirmados() {
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getWritableDatabase();

                ArrayList<String> ListaPedidos = new ArrayList<String>();
                String selectQuery = "SELECT * FROM pedidos_pendientes";
                Cursor cursor = bd.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {    //por cada fila de la tabla se escribe una celda del array
                        ListaPedidos.add(/*cursor.getString(0)*/getNumeroPedido() + " , " + cursor.getString(3) + " , " + cursor.getString(4) + " , " + cursor.getString(5) + " , " + cursor.getString(6) + " , " +
                                cursor.getString(2) + " , " + cursor.getString(7) + " , " + cursor.getString(8) + " , " + cursor.getString(9) + " , " + cursor.getString(10) + " , " +
                                cursor.getString(11) + " , " + cursor.getString(12) + " , " + cursor.getString(13) + " , " + TotalTxt.getText().toString() + " , " + cursor.getString(14) + " , " + cursor.getString(15) + " , " +
                                cursor.getString(16));
                        Log.d("ListaPedidos, pos0: ", ListaPedidos.get(0));
                    } while (cursor.moveToNext());  //avanzar al producto siguiente en el listado
                }
                cursor.close();
                bd.close();
                return (ListaPedidos);
            }

            public void cantidadNuncaNull() {
                //Toast.makeText(Pedidos.this, "Se entra en cantidad nunca null()", Toast.LENGTH_SHORT).show();
                if (CantidadTxt.getText().toString().equals("")) {
                    CantidadTxt.setText("0");
                }
            }
            public void descuentoNuncaNull() {
                if (DescuentoProductoTxt.getText().toString().equals("")) {
                    DescuentoProductoTxt.setText("0");
                }
            }
            public boolean consultarSiHayProductosEnCarrito(){
                Boolean hayProductos = false;
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getWritableDatabase();

                String count = "SELECT COUNT(*) FROM pedidos_pendientes";
                Cursor mcursor = bd.rawQuery(count, null);
                mcursor.moveToFirst();
                int icount = mcursor.getInt(0);
                if(icount>0){
                    hayProductos = true;
                }
                else{
                    hayProductos = false;
                }
                return hayProductos;
            }


            private int getCantidadMinima(String producto) {
                BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
                SQLiteDatabase bd = admin.getReadableDatabase();
                String selectQuery = "SELECT * FROM Productos WHERE nombre = '" + producto + "'";
                Cursor cursor = bd.rawQuery(selectQuery, null);
                int cantidad_minima = 0;
                if (cursor.moveToFirst()) {         //falso si el cursor esta vacio
                    cantidad_minima = Integer.parseInt(cursor.getString(4));
                }

                cursor.close();
                bd.close();
                return cantidad_minima;
            }

            public static boolean esMultiplo(int n1,int n2){
                if (n1%n2==0)
                    return true;
                else
                    return false;
            }

        public boolean validarCantidadIngresada() {
            if (cantidad_minima_de_venta > 1) {
                if ((Integer.parseInt(CantidadTxt.getText().toString()) > 1)) {
                    if (esMultiplo(Integer.parseInt(CantidadTxt.getText().toString()), cantidad_minima_de_venta)) {
                        Toast.makeText(getApplicationContext(), "La cantidad se permite!", Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Solo se venden multiplos de " + cantidad_minima_de_venta, Toast.LENGTH_SHORT).show();
                        CantidadTxt.setText("");
                        return false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Solo se venden multiplos de " + cantidad_minima_de_venta, Toast.LENGTH_SHORT).show();
                    CantidadTxt.setText("");
                    return false;
                }
            }else{
                return false;
            }
        }
    private String direccionDelClienteElegido(String cliente) {
        BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        String selectQuery = "SELECT * FROM Clientes WHERE razonSocial = '" + cliente + "'";
        Cursor cursor = bd.rawQuery(selectQuery, null);
        String direccion = "";
        if (cursor.moveToFirst()) {         //falso si el cursor esta vacio
            direccion = cursor.getString(7);
        }
        cursor.close();
        bd.close();
        return direccion;
    }
}

