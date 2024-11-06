package com.example.proyectoutilidades;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivitySensores extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscope;
    private TextView txtX, txtY, txtZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitysensores);

        // Referencias a los campos de texto
        txtX = findViewById(R.id.txtX);
        txtY = findViewById(R.id.txtY);
        txtZ = findViewById(R.id.txtZ);

        // Inicializar el SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Verificar si el dispositivo tiene giroscopio
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope == null) {
            Toast.makeText(this, "El giroscopio no está disponible en este dispositivo", Toast.LENGTH_LONG).show();
            finish();  // Cierra la actividad si no hay giroscopio
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el SensorEventListener para el giroscopio
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el SensorEventListener para el giroscopio
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Leer los valores de los tres ejes X, Y, Z
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Mostrar los valores en los TextViews correspondientes
            txtX.setText(String.format("Eje X: %.2f m/s²", x));
            txtY.setText(String.format("Eje Y: %.2f m/s²", y));
            txtZ.setText(String.format("Eje Z: %.2f m/s²", z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Aquí puedes manejar cambios en la precisión del sensor si es necesario
    }
}