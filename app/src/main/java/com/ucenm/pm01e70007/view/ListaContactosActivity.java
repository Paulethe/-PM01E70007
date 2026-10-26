package com.ucenm.pm01e70007.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.ucenm.pm01e70007.R;
import com.ucenm.pm01e70007.controller.ContactoController;
import com.ucenm.pm01e70007.model.Contactos;
import com.ucenm.pm01e70007.util.Formato;
import com.ucenm.pm01e70007.util.ImagenUtil;
import com.ucenm.pm01e70007.util.Paises;

import java.util.List;

/** PANTALLA 2: lista de contactos */
public class ListaContactosActivity extends AppCompatActivity {

    private ContactoController controller;
     private ContactoAdapter adapter;
    private Contactos seleccionado;

    private EditText etBuscar;
    private TextView tvVacio, tvSelNombre, tvSelTelefono;
    private View panel;
    private ShapeableImageView imgSel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        controller = new ContactoController(this);

        etBuscar = findViewById(R.id.etBuscar);
        tvVacio = findViewById(R.id.tvVacio);
        panel = findViewById(R.id.panelSeleccion);
        imgSel = findViewById(R.id.imgSel);
        tvSelNombre = findViewById(R.id.tvSelNombre);
        tvSelTelefono = findViewById(R.id.tvSelTelefono);

        RecyclerView rv = findViewById(R.id.rvContactos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactoAdapter(this::seleccionar);
        rv.setAdapter(adapter);

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void afterTextChanged(Editable s) { cargar(); }
        });

        findViewById(R.id.btnActualizar).setOnClickListener(v -> actualizar());
        findViewById(R.id.btnEliminar).setOnClickListener(v -> confirmarEliminar());
        findViewById(R.id.btnCompartir).setOnClickListener(v -> compartir());
        findViewById(R.id.btnVerImagen).setOnClickListener(v -> verImagen());
        findViewById(R.id.btnLlamar).setOnClickListener(v -> llamar());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    private void cargar() {
        List<Contactos> lista = controller.buscar(etBuscar.getText().toString());
        adapter.setDatos(lista);
        tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        if (seleccionado != null) {
            Contactos vigente = null;
            for (Contactos c : lista) {
                if (c.getId() == seleccionado.getId()) { vigente = c; break; }
            }
            if (vigente != null) {
                seleccionar(vigente);
            } else {
                seleccionado = null;
                panel.setVisibility(View.GONE);
            }
        }
    }

    private void seleccionar(Contactos c) {
        seleccionado = c;
        adapter.setSeleccionado(c.getId());
        panel.setVisibility(View.VISIBLE);
        tvSelNombre.setText(c.getNombre());
        tvSelTelefono.setText(Formato.telefono(c.getTelefono()));
        ImagenUtil.mostrar(imgSel, c.getImagen(), R.drawable.ic_person, 10);
    }

    private void actualizar() {
        if (seleccionado == null) return;
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.EXTRA_ID, seleccionado.getId());
        startActivity(i);
    }

    private void confirmarEliminar() {
        if (seleccionado == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Eliminar contacto")
                .setMessage("¿Desea eliminar a " + seleccionado.getNombre() + "?")
                .setPositiveButton("Sí", (d, w) -> {
                    if (controller.eliminar(seleccionado.getId())) {
                        Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                        seleccionado = null;
                        panel.setVisibility(View.GONE);
                        adapter.setSeleccionado(-1);
                        cargar();
                    } else {
                        Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void compartir() {
        if (seleccionado == null) return;
        String codigo = Paises.codigoDe(seleccionado.getPais());
        String texto = "Nombre: " + seleccionado.getNombre()
                + "\nTeléfono: +" + codigo + " " + seleccionado.getTelefono();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Contacto: " + seleccionado.getNombre());
        i.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(i, "Compartir contacto"));
    }

    private void verImagen() {
        if (seleccionado == null) return;
        Bitmap bmp = ImagenUtil.bytesABitmap(seleccionado.getImagen());
        if (bmp == null) {
            Toast.makeText(this, "Este contacto no tiene imagen guardada", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageView iv = new ImageView(this);
        iv.setAdjustViewBounds(true);
        iv.setImageBitmap(bmp);
        int p = Math.round(16 * getResources().getDisplayMetrics().density);
        iv.setPadding(p, p, p, p);
        new AlertDialog.Builder(this)
                .setTitle(seleccionado.getNombre())
                .setView(iv)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void llamar() {
        if (seleccionado == null) return;
        Intent i = new Intent(this, LlamarActivity.class);
        i.putExtra(MainActivity.EXTRA_ID, seleccionado.getId());
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
