package com.ucenm.pm01e70007.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.ucenm.pm01e70007.R;
import com.ucenm.pm01e70007.model.Contactos;
import com.ucenm.pm01e70007.util.Formato;
import com.ucenm.pm01e70007.util.ImagenUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.VH> {

    public interface OnContactoClick {
        void onClick(Contactos contacto);
    }

    private List<Contactos> datos = new ArrayList<>();
    private int seleccionadoId = -1;
    private final OnContactoClick listener;

    public ContactoAdapter(OnContactoClick listener) {
        this.listener = listener;
    }

    public void setDatos(List<Contactos> nuevos) {
        this.datos = nuevos;
        notifyDataSetChanged();
    }

    public void setSeleccionado(int id) {
        this.seleccionadoId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        final Contactos c = datos.get(position);
        h.nombre.setText(c.getNombre());
        h.telefono.setText(Formato.telefono(c.getTelefono()));
        ImagenUtil.mostrar(h.foto, c.getImagen(), R.drawable.ic_person, 10);
        h.itemView.setBackgroundColor(c.getId() == seleccionadoId
                ? h.itemView.getResources().getColor(R.color.azul_claro) : Color.TRANSPARENT);
        h.itemView.setOnClickListener(v -> {
            seleccionadoId = c.getId();
            notifyDataSetChanged();
            listener.onClick(c);
        });
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ShapeableImageView foto;
        final TextView nombre, telefono;

        VH(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.imgFoto);
            nombre = itemView.findViewById(R.id.tvNombre);
            telefono = itemView.findViewById(R.id.tvTelefono);
        }
    }
}
