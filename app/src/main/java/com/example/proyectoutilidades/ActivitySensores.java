package com.example.proyectoutilidades;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitySensores extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager = null;
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
        try{
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Verificar si el dispositivo tiene sensor de rotación
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rotationVectorSensor == null  ) {
            Toast.makeText(this, "El sensor de rotación no está disponible en este dispositivo", Toast.LENGTH_LONG).show();
            finish();  // Cierra la actividad si no hay sensor de rotación
        }

        }catch(Exception ex){
            Toast.makeText(this, "Error al iniciar el sensor. "+ex.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace(); // Registra los errores en los logs
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Registrar el SensorEventListener para el sensor de rotación
            if (rotationVectorSensor != null) {
                sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }catch(Exception ex){
            Toast.makeText(this, "No se pudo activar el sensor de rotación. Intenta reiniciar la aplicación."+ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("MiApp", "Error al registrar el sensor de rotación", ex);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            // Desregistrar el SensorEventListener para el sensor de rotación
            sensorManager.unregisterListener(this);
        }catch(Exception ex){
            Toast.makeText(this, "No se pudo desactivar el sensor de rotación. Intenta reiniciar la aplicación."+ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Mi App", "Error al desregistrar el sensor de rotación", ex);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                if (event.values == null || event.values.length < 3) {
                    Log.w("MiApp", "Datos del sensor incompletos o nulos.");
                    return;
                }

                float[] rotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

                float[] orientationAngles = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientationAngles);

                float z = (float) Math.toDegrees(orientationAngles[0]);
                float y = (float) Math.toDegrees(orientationAngles[1]);
                float x = (float) Math.toDegrees(orientationAngles[2]);

                if (txtX != null && txtY != null && txtZ != null) {
                    txtX.setText(String.format("Eje X: %.2f°", x));
                    txtY.setText(String.format("Eje Y: %.2f°", y));
                    txtZ.setText(String.format("Eje Z: %.2f°", z));
                } else {
                    Log.e("MiApp", "Los TextViews no están inicializados.");
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Error al procesar los datos del sensor. Intenta reiniciar la aplicación.", Toast.LENGTH_LONG).show();
            Log.e("MiApp", "Error al procesar los datos del sensor de rotación", ex);
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
