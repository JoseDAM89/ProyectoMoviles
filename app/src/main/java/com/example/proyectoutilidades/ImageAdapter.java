package com.example.proyectoutilidades;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;

public class ImageAdapter extends BaseAdapter {
    private final Context context;
    private final File[] images;

    public ImageAdapter(Context context, File[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
        } else {
            imageView = (ImageView) convertView;
        }

        // Cargar la imagen
        Bitmap bitmap = BitmapFactory.decodeFile(images[position].getAbsolutePath());
        imageView.setImageBitmap(bitmap);

        // Configurar contenido accesible
        String description = "Imagen " + (position + 1) + ": Imagen capturada por la cámara.";
        imageView.setContentDescription(description);

        return imageView;
    }
}
