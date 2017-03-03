package com.example.suiza.pedidos_en_sony;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

public class ProductosDia extends AppCompatActivity {
    TableLayout tabla_productos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_dia);

        /******************************************************/
        tabla_productos = (TableLayout) findViewById(R.id.TablaProductos);
        BuildTable();

    }

    private void BuildTable() {
            BD admin = new BD(this, BD.NAME, BD.CURSORFACTORY, BD.VERSION);
            SQLiteDatabase bd = admin.getReadableDatabase();
        try {
            String sql = "SELECT * FROM Productos";
            Cursor cursor = bd.rawQuery(sql, null);
            if (cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    TableRow row = new TableRow(this);
                    row.setLayoutParams(new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView tv1 = new TextView(this);
                    tv1.setLayoutParams(new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    tv1.setGravity(Gravity.LEFT);
                    tv1.setTextSize(10);
                    tv1.setText("ID");
                    row.addView(tv1);

                    TextView tv2 = new TextView(this);
                    tv2.setLayoutParams(new TableRow.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    tv2.setGravity(Gravity.LEFT);
                    tv2.setTextSize(10);
                    tv2.setText("Nombre");
                    row.addView(tv2);

                    TextView tv3 = new TextView(this);
                    tv3.setLayoutParams(new TableRow.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    tv3.setGravity(Gravity.LEFT);
                    tv3.setTextSize(10);
                    tv3.setText("Precio Unitario");
                    row.addView(tv3);

                    TextView tv4 = new TextView(this);
                    tv4.setLayoutParams(new TableRow.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    tv4.setGravity(Gravity.LEFT);
                    tv4.setTextSize(10);
                    tv4.setText("");
                    row.addView(tv4);

                    do {
                        int cols = cursor.getColumnCount();


                        for (int j = 0; j < cols + 1; j++) {
                            if (j == cols) {
                                CheckBox cb = new CheckBox(this);
                                cb.setLayoutParams(new TableRow.LayoutParams(
                                        TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                cb.setGravity(Gravity.LEFT);
                                row.addView(cb);
                                final int k = j;
                                cb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //.add(Integer.toString(k));
                                    }
                                });
                                break;
                            }


                            TextView tv = new TextView(this);
                            tv.setLayoutParams(new TableRow.LayoutParams(
                                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            tv.setGravity(Gravity.LEFT);
                            tv.setTextSize(10);
                            tv.setText(cursor.getString(j));

                            row.addView(tv);

                        }

                        tabla_productos.addView(row);
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception mSQLException) {
            Toast.makeText(ProductosDia.this,""+mSQLException,Toast.LENGTH_LONG);
        }
    }
}
