package com.ucenm.pm01e70007.view;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.ucenm.pm01e70007.R;
import com.ucenm.pm01e70007.controller.ContactoController;
import com.ucenm.pm01e70007.model.Contactos;
import com.ucenm.pm01e70007.util.Formato;
import com.ucenm.pm01e70007.util.ImagenUtil;
import com.ucenm.pm01e70007.util.Paises;

/** Confirmar y realizar la llamada */
public class LlamarActivity extends AppCompatActivity {

    private Contactos contacto;
    private String codigoPais;

    private final ActivityResultLauncher<String> permisoLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (concedido) {
                    realizarLlamada();
                } else {
                    Toast.makeText(this, "Permiso de llamadas denegado", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ContactoController controller = new ContactoController(this);
        contacto = controller.obtener(getIntent().getIntExtra(MainActivity.EXTRA_ID, -1));
        if (contacto == null) {
            Toast.makeText(this, "Contacto no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        codigoPais = Paises.codigoDe(contacto.getPais());

        ShapeableImageView img = findViewById(R.id.imgLlamar);
        ImagenUtil.mostrar(img, contacto.getImagen(), R.drawable.ic_person, 24);
        ((TextView) findViewById(R.id.tvLlamarNombre)).setText(contacto.getNombre());
        ((TextView) findViewById(R.id.tvLlamarTelefono))
                .setText("+" + codigoPais + " " + Formato.telefono(contacto.getTelefono()));

        findViewById(R.id.btnLlamarAhora).setOnClickListener(v -> mostrarConfirmacion());

        if (savedInstanceState == null) {
            mostrarConfirmacion();
        }
    }

    private void mostrarConfirmacion() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar llamada")
                .setMessage("¿Desea llamar a " + contacto.getNombre() + "?\n"
                        + Formato.telefono(contacto.getTelefono()))
                .setPositiveButton("Sí", (d, w) -> realizarLlamada())
                .setNegativeButton("No", null)
                .show();
    }

    private void realizarLlamada() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            permisoLauncher.launch(Manifest.permission.CALL_PHONE);
            return;
        }
        Intent i = new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:+" + codigoPais + contacto.getTelefono()));
        try {
            startActivity(i);
        } catch (SecurityException | ActivityNotFoundException e) {
            Toast.makeText(this, "No se pudo realizar la llamada", Toast.LENGTH_LONG).show();
        }
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
