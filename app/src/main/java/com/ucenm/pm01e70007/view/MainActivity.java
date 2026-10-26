package com.ucenm.pm01e70007.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.ucenm.pm01e70007.R;
import com.ucenm.pm01e70007.controller.ContactoController;
import com.ucenm.pm01e70007.model.Contactos;
import com.ucenm.pm01e70007.util.ImagenUtil;
import com.ucenm.pm01e70007.util.Paises;
import com.ucenm.pm01e70007.util.Validador;

/** Registro (y actualización) de contacto. */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "contacto_id";

    private ContactoController controller;
    private ShapeableImageView imgContacto;
    private Spinner spPais;
    private EditText etNombre, etTelefono, etNota;
    private MaterialButton btnGuardar;

    private byte[] imagenBytes = null;
    private int contactoId = -1;

    private final ActivityResultLauncher<Void> camaraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) {
                    imagenBytes = ImagenUtil.bitmapABytes(bitmap);
                    mostrarImagen();
                }
            });

    private final ActivityResultLauncher<String> galeriaLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    byte[] datos = ImagenUtil.uriABytes(getContentResolver(), uri);
                    if (datos != null) {
                        imagenBytes = datos;
                        mostrarImagen();
                    } else {
                        Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new ContactoController(this);

        imgContacto = findViewById(R.id.imgContacto);
        spPais = findViewById(R.id.spPais);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etNota = findViewById(R.id.etNota);
        btnGuardar = findViewById(R.id.btnGuardar);
        MaterialButton btnVer = findViewById(R.id.btnVerContactos);

        spPais.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                Paises.etiquetas()));
        spPais.setSelection(Paises.indice("Honduras"));

        // Expresiones regulares: rechazan caracteres inválidos mientras se escribe
        etNombre.setFilters(new InputFilter[]{Validador.filtroNombre()});
        etTelefono.setFilters(new InputFilter[]{Validador.filtroTelefono(), new InputFilter.LengthFilter(15)});

        View.OnClickListener elegirImagen = v -> mostrarOpcionesImagen();
        imgContacto.setOnClickListener(elegirImagen);
        findViewById(R.id.tvAgregarImagen).setOnClickListener(elegirImagen);

        btnGuardar.setOnClickListener(v -> guardar());
        btnVer.setOnClickListener(v -> startActivity(new Intent(this, ListaContactosActivity.class)));

        // Modo actualización: se reutiliza este mismo formulario
        contactoId = getIntent().getIntExtra(EXTRA_ID, -1);
        if (contactoId != -1) {
            cargarContacto();
        } else {
            mostrarImagen();
        }
    }

    private void cargarContacto() {
        Contactos c = controller.obtener(contactoId);
        if (c == null) {
            finish();
            return;
        }
        setTitle("Actualizar contacto");
        btnGuardar.setText("Actualizar contacto");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spPais.setSelection(Paises.indice(c.getPais()));
        etNombre.setText(c.getNombre());
        etTelefono.setText(c.getTelefono());
        etNota.setText(c.getNota());
        imagenBytes = c.getImagen();
        mostrarImagen();
    }

    private void mostrarImagen() {
        ImagenUtil.mostrar(imgContacto, imagenBytes, R.drawable.ic_camera, 28);
    }

    private void mostrarOpcionesImagen() {
        String[] opciones = {"Tomar foto", "Elegir de la galería"};
        new AlertDialog.Builder(this)
                .setTitle("Imagen del contacto")
                .setItems(opciones, (d, which) -> {
                    if (which == 0) {
                        try {
                            camaraLauncher.launch(null);
                        } catch (Exception e) {
                            Toast.makeText(this, "No hay cámara disponible", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        galeriaLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String nota = etNota.getText().toString().trim();

        ContactoController.ErrorCampo error = controller.validar(nombre, telefono, nota);
        if (error != null) {
            mostrarAlerta(error);
            return;
        }

        String pais = Paises.LISTA.get(spPais.getSelectedItemPosition()).getNombre();
        Contactos c = new Contactos(contactoId, pais, nombre, telefono, nota, imagenBytes);

        if (contactoId == -1) {
            long r = controller.guardar(c);
            if (r > 0) {
                Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
            } else {
                Toast.makeText(this, "Error al guardar el contacto", Toast.LENGTH_LONG).show();
            }
        } else {
            if (controller.actualizar(c) > 0) {
                Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar el contacto", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void mostrarAlerta(ContactoController.ErrorCampo error) {
        final EditText foco;
        switch (error.campo) {
            case ContactoController.ErrorCampo.NOMBRE: foco = etNombre; break;
            case ContactoController.ErrorCampo.TELEFONO: foco = etTelefono; break;
            default: foco = etNota; break;
        }
        new AlertDialog.Builder(this)
                .setTitle("Dato requerido")
                .setMessage(error.mensaje)
                .setPositiveButton("Aceptar", (d, w) -> foco.requestFocus())
                .show();
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etTelefono.setText("");
        etNota.setText("");
        spPais.setSelection(Paises.indice("Honduras"));
        imagenBytes = null;
        mostrarImagen();
        etNombre.requestFocus();
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
