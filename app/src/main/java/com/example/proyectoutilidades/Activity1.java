package com.example.proyectoutilidades;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Activity1 extends AppCompatActivity {

    private static final String CLAVE_CONTADOR = "ArchivoContador";
    private static final String VALOR_CONTADOR = "Contador";

    private TextView textViewCounter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity1); // Asegúrate de que este es el nombre correcto del layout

        aumentacontador();

    }

    private void aumentacontador() {

        // Inicializar el TextView
        textViewCounter = findViewById(R.id.textViewCounter);

        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences(CLAVE_CONTADOR, MODE_PRIVATE);

        // Recuperar el contador guardado
        int counter = sharedPreferences.getInt(VALOR_CONTADOR, 0);



        // Mostrar el contador en el TextView
        textViewCounter.setText("Número de accesos a la App: " + counter);

        // Incrementar el contador
        counter++;

        // Guardar el contador actualizado
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VALOR_CONTADOR, counter);
        editor.apply(); // Aplica los cambios
    }
}
