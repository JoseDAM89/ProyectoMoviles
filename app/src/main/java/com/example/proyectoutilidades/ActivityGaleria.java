package com.example.proyectoutilidades;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
        images = directory.listFiles();

        if (images != null && images.length > 0) {
            // Mostrar las imágenes en el GridView
            gridView.setAdapter(new ImageAdapter(this, images));
            noImagesText.setVisibility(View.GONE); // Ocultar el texto si hay imágenes
        } else {
            // Mostrar el texto si no hay imágenes
            noImagesText.setVisibility(View.VISIBLE);
        }

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ActivityImagenAmpliada.class);
            intent.putExtra("imagePath", images[position].getAbsolutePath());
            startActivity(intent);
        });
    }
}
