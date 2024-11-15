package com.example.proyectoutilidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ActivityImagenAmpliada extends AppCompatActivity {

    private ImageView imageView;
    private String imagePath;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityimagenampliada);

        // Obtener la referencia a los elementos de la UI
        imageView = findViewById(R.id.imageView);
        imagePath = getIntent().getStringExtra("imagePath");

        // Cargar la imagen en el ImageView
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);

        // Inicializar el ScaleGestureDetector
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Configurar el botón de eliminar
        Button buttonEliminar = findViewById(R.id.buttonEliminar);
        buttonEliminar.setOnClickListener(v -> {
            File file = new File(imagePath);
            if (file.delete()) {
                Toast.makeText(this, "Imagen eliminada", Toast.LENGTH_SHORT).show();
                finish(); // Regresar a la galería
            } else {
                Toast.makeText(this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Detectar los gestos de toque en la pantalla
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pasar el evento al ScaleGestureDetector
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    // Implementación de la clase ScaleListener para manejar el gesto de zoom
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Obtener el factor de escala del gesto
            scaleFactor *= detector.getScaleFactor();
            // Limitar el factor de escala entre 0.1 y 10
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
            // Aplicar la escala a la imagen
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            return true;
        }
    }
}
