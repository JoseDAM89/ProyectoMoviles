package com.example.proyectoutilidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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

        imageView = findViewById(R.id.imageView);
        imagePath = getIntent().getStringExtra("imagePath");

        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(this, "Ruta de imagen no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        File imageFile = new File(imagePath);
        if (!imageFile.exists() || !imageFile.canRead()) {
            Toast.makeText(this, "No se puede acceder a la imagen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        findViewById(R.id.buttonEliminar).setOnClickListener(v -> eliminarImagen());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void eliminarImagen() {
        File file = new File(imagePath);
        if (file.delete()) {
            Toast.makeText(this, "Imagen eliminada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 5.0f));
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            return true;
        }
    }
}
