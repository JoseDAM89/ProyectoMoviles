package com.example.proyectoutilidades;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ActivityGaleria extends AppCompatActivity {
    private GridView gridView;
    private TextView noImagesText;
    private File[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitygaleria);

        gridView = findViewById(R.id.gridView);
        noImagesText = findViewById(R.id.noImagesText);

        File directory = new File(getFilesDir(), "MisFotos");
        if (!directory.exists() || !directory.isDirectory()) {
            Toast.makeText(this, "El directorio no existe o no es válido.", Toast.LENGTH_SHORT).show();
            noImagesText.setVisibility(View.VISIBLE);
            return;
        }

        images = directory.listFiles();
        Log.d("ActivityGaleria", "Total imágenes: " + (images != null ? images.length : 0));

        if (images != null && images.length > 0) {
            gridView.setAdapter(new ImageAdapter(this, images));
            noImagesText.setVisibility(View.GONE);
        } else {
            noImagesText.setVisibility(View.VISIBLE);
        }

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                if (images == null || position < 0 || position >= images.length) {
                    throw new Exception("Índice fuera de rango o lista de imágenes nula.");
                }

                File selectedFile = images[position];
                Log.d("ActivityGaleria", "Archivo seleccionado: " + (selectedFile != null ? selectedFile.getAbsolutePath() : "null"));

                if (selectedFile != null && selectedFile.isFile() && selectedFile.canRead()) {
                    Intent intent = new Intent(this, ActivityImagenAmpliada.class);
                    intent.putExtra("imagePath", selectedFile.getAbsolutePath());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "El archivo no es válido o no se puede leer.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error al abrir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
