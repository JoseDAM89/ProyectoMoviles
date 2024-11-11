package com.example.proyectoutilidades;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityCamara extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private ImageView imageView;
    private Button buttonTakePhoto;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitycamara);

        imageView = findViewById(R.id.imageView);
        buttonTakePhoto = findViewById(R.id.buttonTakePhoto);

        // Verificar si la carpeta MisFotos existe, y crearla si no
        File storageDir = new File(getFilesDir(), "MisFotos");
        if (!storageDir.exists()) {
            storageDir.mkdirs(); // Crear la carpeta si no existe
        }

        buttonTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CAMERA_PERMISSION);
            } else {
                openCamera();
            }
        });
    }

    // Método para abrir la cámara
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                // Crea el archivo donde se guardará la foto
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.proyectoutilidades.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Método para crear el archivo de imagen
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Crear un directorio para guardar la imagen dentro del almacenamiento interno
        File storageDir = new File(getFilesDir(), "MisFotos"); // Usamos el directorio privado de la app
        if (!storageDir.exists()) {
            storageDir.mkdirs(); // Crear la carpeta si no existe
        }

        // Crear el archivo de imagen
        File image = File.createTempFile(
                imageFileName,  /* prefijo del archivo */
                ".jpg",         /* sufijo */
                storageDir      /* directorio */
        );

        return image;
    }

    // Maneja el resultado de la captura de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Mostrar la foto en el ImageView desde el archivo guardado
            imageView.setImageURI(Uri.fromFile(photoFile));
            Toast.makeText(this, "Imagen guardada en: " + photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
    }

    // Método para cargar la imagen desde almacenamiento interno
    public Bitmap cargarImagenDesdeInterno(String nombreArchivo) {
        File storageDir = new File(getFilesDir(), "MisFotos");
        File imageFile = new File(storageDir, nombreArchivo);

        if (imageFile.exists()) {
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }
        return null;
    }

    // Método para mostrar la imagen en un ImageView
    public void mostrarImagen(String nombreArchivo) {
        Bitmap bitmap = cargarImagenDesdeInterno(nombreArchivo);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Imagen no encontrada", Toast.LENGTH_SHORT).show();
        }
    }

    // Maneja la solicitud de permisos de cámara
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


