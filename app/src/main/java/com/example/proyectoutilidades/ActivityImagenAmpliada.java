package com.example.proyectoutilidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ActivityImagenAmpliada extends AppCompatActivity {
    private ImageView imageView;
    private String imagePath;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    // Coordenadas y dimensiones originales de la miniatura
    private int left;
    private int top;
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityimagenampliada);

        imageView = findViewById(R.id.imageView);
        imagePath = getIntent().getStringExtra("imagePath");

        // Verificar si la imagen es válida
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

        // Cargar la imagen en el ImageView
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Error al cargar la imagen.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar gestos de escalado
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Recuperar coordenadas y dimensiones de la miniatura
        left = getIntent().getIntExtra("left", 0);
        top = getIntent().getIntExtra("top", 0);
        width = getIntent().getIntExtra("width", 0);
        height = getIntent().getIntExtra("height", 0);

        // Validar dimensiones
        if (width <= 0 || height <= 0) {
            Toast.makeText(this, "Dimensiones inválidas para la animación.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Asegurarse de que el ImageView está dibujado antes de animar
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Iniciar la animación de expansión
                animateImageExpansion();
            }
        });

        // Configurar el botón de eliminación
        View buttonEliminar = findViewById(R.id.buttonEliminar);
        if (buttonEliminar != null) {
            buttonEliminar.setOnClickListener(v -> eliminarImagen());
        } else {
            Toast.makeText(this, "Botón de eliminar no encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        // Iniciar animación inversa antes de cerrar
        animateImageCollapse();
    }

    // Método para eliminar la imagen
    private void eliminarImagen() {
        File file = new File(imagePath);
        if (file.delete()) {
            Toast.makeText(this, "Imagen eliminada", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.vengo, R.anim.me_voy); // Animaciones de salida personalizadas
        } else {
            Toast.makeText(this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para animar la expansión de la imagen
    private void animateImageExpansion() {
        imageView.setPivotX(0);
        imageView.setPivotY(0);
        imageView.setScaleX((float) width / imageView.getWidth());
        imageView.setScaleY((float) height / imageView.getHeight());
        imageView.setTranslationX(left);
        imageView.setTranslationY(top);

        imageView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .translationX(0)
                .translationY(0)
                .setDuration(300) // Duración de la animación
                .start();
    }

    // Método para animar la colisión de la imagen
    private void animateImageCollapse() {
        imageView.animate()
                .scaleX((float) width / imageView.getWidth())
                .scaleY((float) height / imageView.getHeight())
                .translationX(left)
                .translationY(top)
                .setDuration(300) // Duración de la animación
                .withEndAction(() -> {
                    finish();
                    overridePendingTransition(0, 0); // Sin animación adicional
                })
                .start();
    }

    // Listener para gestos de escalado
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


/* SIN ANIMACIÓN PARA AMPLIAR, TAMBIÉN CAMBIAR ACTIVITY GALERÍA  itemView.setOnClickListener......

package com.example.proyectoutilidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
            overridePendingTransition(R.anim.vengo, R.anim.me_voy); // Sin animación de entrada, solo salida
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
}*/
