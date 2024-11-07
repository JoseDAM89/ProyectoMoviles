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
    private Sensor rotationVectorSensor;
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

        // Verificar si el dispositivo tiene sensor de rotación
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotationVectorSensor == null) {
            Toast.makeText(this, "El sensor de rotación no está disponible en este dispositivo", Toast.LENGTH_LONG).show();
            finish();  // Cierra la actividad si no hay sensor de rotación
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el SensorEventListener para el sensor de rotación
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el SensorEventListener para el sensor de rotación
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            // Convertir la matriz de rotación a ángulos de orientación
            float[] orientationAngles = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            // Convertir los ángulos de radianes a grados
            float z = (float) Math.toDegrees(orientationAngles[0]); // Z (Eje)
            float y = (float) Math.toDegrees(orientationAngles[1]);   // X (Inclinación)
            float x = (float) Math.toDegrees(orientationAngles[2]);    // Y (Rotación)

            // Mostrar los valores en los TextViews correspondientes
            txtX.setText(String.format("Eje X: %.2f°", x));
            txtY.setText(String.format("Eje Y: %.2f°", y));
            txtZ.setText(String.format("Eje Z: %.2f°", z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Verifica si el sensor afectado es el de rotación (Rotation Vector)
        if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    // Alta precisión
                    Toast.makeText(this, "Alta precisión en el sensor de rotación.", Toast.LENGTH_SHORT).show();
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    // Precisión media
                    Toast.makeText(this, "Precisión media en el sensor de rotación.", Toast.LENGTH_SHORT).show();
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    // Baja precisión
                    Toast.makeText(this, "Baja precisión en el sensor de rotación. Los valores pueden no ser exactos.", Toast.LENGTH_SHORT).show();
                    break;
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    // Precisión no confiable
                    Toast.makeText(this, "Precisión no confiable en el sensor de rotación. Intentando recalibrar...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

}
