package com.example.proyectoutilidades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class ActivityGaleria extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView noImagesText;
    private File[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitygaleria);

        recyclerView = findViewById(R.id.recyclerView); // Cambiado el ID a recyclerView
        noImagesText = findViewById(R.id.noImagesText);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // Ajusta el número de columnas

        actualizarGaleria();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarGaleria();
    }

    private void actualizarGaleria() {
        File directory = new File(getFilesDir(), "MisFotos");
        if (!directory.exists() || !directory.isDirectory()) {
            Toast.makeText(this, "El directorio no existe o no es válido.", Toast.LENGTH_SHORT).show();
            noImagesText.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null); // Limpia el RecyclerView
            return;
        }

        images = directory.listFiles();
        Log.d("ActivityGaleria", "Total imágenes actualizadas: " + (images != null ? images.length : 0));

        if (images != null && images.length > 0) {
            recyclerView.setAdapter(new ImageRecyclerAdapter(images));
            noImagesText.setVisibility(View.GONE);
        } else {
            noImagesText.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null); // Limpia el RecyclerView si no hay imágenes
        }

    }

    private class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder> {
        private final File[] images;

        public ImageRecyclerAdapter(File[] images) {
            this.images = images;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            File imageFile = images[position];
            holder.bind(imageFile);
        }

        @Override
        public int getItemCount() {
            return images.length;
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageView;

            public ImageViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        File selectedFile = images[position];
                        if (selectedFile.isFile() && selectedFile.canRead()) {
                            Intent intent = new Intent(ActivityGaleria.this, ActivityImagenAmpliada.class);
                            intent.putExtra("imagePath", selectedFile.getAbsolutePath());
                            startActivity(intent);
                        } else {
                            Toast.makeText(ActivityGaleria.this, "El archivo no es válido o no se puede leer.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            public void bind(File imageFile) {
                if (imageFile != null && imageFile.exists()) {
                    try {
                        // Obtener el ancho y alto del ImageView, usar valores predeterminados si son 0
                        int reqWidth = imageView.getWidth() > 0 ? imageView.getWidth() : 200; // 200 es un ejemplo
                        int reqHeight = imageView.getHeight() > 0 ? imageView.getHeight() : 200;

                        // Escalar la imagen para que se ajuste al tamaño del ImageView
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true; // Solo lee las dimensiones
                        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

                        // Calcular el tamaño de muestra adecuado para la imagen
                        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                        options.inJustDecodeBounds = false; // Ahora sí cargamos el bitmap

                        // Decodificar la imagen con el tamaño escalado
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            // Placeholder si la imagen no se puede cargar
                            imageView.setImageResource(R.drawable.ic_launcher_foreground);
                        }
                    } catch (Exception e) {
                        // Manejo de excepciones si ocurre un error
                        e.printStackTrace();
                        imageView.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                } else {
                    // Placeholder si el archivo no existe
                    imageView.setImageResource(R.drawable.ic_launcher_foreground);
                }
            }

            private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
                int height = options.outHeight;
                int width = options.outWidth;
                int inSampleSize = 1;

                if (height > reqHeight || width > reqWidth) {
                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    // Calcular el mayor factor de escala que sea una potencia de 2
                    while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                }
                return inSampleSize;
            }



        }
    }
}
