package com.example.proyectoutilidades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

        recyclerView = findViewById(R.id.recyclerView);
        noImagesText = findViewById(R.id.noImagesText);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // Número de columnas
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 50, true)); // Márgenes de 16dp

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
            recyclerView.setAdapter(null);
            return;
        }

        images = directory.listFiles();
        Log.d("ActivityGaleria", "Total imágenes actualizadas: " + (images != null ? images.length : 0));

        if (images != null && images.length > 0) {
            recyclerView.setAdapter(new ImageRecyclerAdapter(images));
            noImagesText.setVisibility(View.GONE);
        } else {
            noImagesText.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null);
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
                            // Obtener las coordenadas y dimensiones de la miniatura seleccionada
                            int[] screenLocation = new int[2];
                            imageView.getLocationOnScreen(screenLocation); // Coordenadas en pantalla
                            int width = imageView.getWidth();
                            int height = imageView.getHeight();

                            // Pasar estos datos a la siguiente actividad
                            Intent intent = new Intent(ActivityGaleria.this, ActivityImagenAmpliada.class);
                            intent.putExtra("imagePath", selectedFile.getAbsolutePath());
                            intent.putExtra("left", screenLocation[0]);
                            intent.putExtra("top", screenLocation[1]);
                            intent.putExtra("width", width);
                            intent.putExtra("height", height);

                            startActivity(intent);
                            overridePendingTransition(0, 0); // Sin animación predeterminada
                        } else {
                            Toast.makeText(ActivityGaleria.this, "El archivo no es válido o no se puede leer.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                /* SIN ANIMACIÓN PARA AMPLIAR, TAMBIÉN CAMBIAR ACTIVITY IMAGEN AMPLIADA
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
                });*/
            }


            public void bind(File imageFile) {
                if (imageFile != null && imageFile.exists()) {
                    try {
                        // Dimensiones deseadas para las imágenes
                        int reqWidth = 100;
                        int reqHeight = 100;

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

                        // Escalado de la imagen según las dimensiones deseadas
                        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                        options.inJustDecodeBounds = false;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;

                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            // Si la imagen no se puede cargar, eliminar el archivo
                            eliminarImagen(imageFile);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Si ocurre un error, eliminar el archivo
                        eliminarImagen(imageFile);
                    }
                } else {
                    // Si el archivo no existe, eliminarlo por seguridad
                    eliminarImagen(imageFile);
                }
            }

            // Método para eliminar la imagen y mostrar un mensaje
            private void eliminarImagen(File imageFile) {
                if (imageFile != null && imageFile.exists()) {
                    if (imageFile.delete()) {
                        actualizarGaleria();
                    }
                }
            }

            private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
                int height = options.outHeight;
                int width = options.outWidth;
                int inSampleSize = 1;

                if (height > reqHeight || width > reqWidth) {
                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    // Calcula el mayor factor de escala que sea potencia de 2
                    while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                }
                return inSampleSize;
            }

        }
    }

    // Clase para decorar con márgenes
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
}
