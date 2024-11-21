package com.example.proyectoutilidades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Activity1 extends AppCompatActivity {

    private static final String CLAVE_CONTADOR = "ArchivoContador";
    private static final String VALOR_CONTADOR = "Contador";
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;


    private TextView textViewCounter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity1); // Asegúrate de que este es el nombre correcto del layout

        aumentacontador();

        btn1 = findViewById(R.id.buttonInternet);
        btn2 = findViewById(R.id.buttonCamara);
        btn3 = findViewById(R.id.buttonSensores);
        btn4 = findViewById(R.id.buttonGaleria);

 

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentInternet = new Intent(Activity1.this, ActivityInternet.class);
                startActivity(intentInternet);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCamara = new Intent(Activity1.this, ActivityCamara.class);
                startActivity(intentCamara);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSensores = new Intent(Activity1.this,ActivitySensores.class);
                startActivity(intentSensores);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGaleria = new Intent(Activity1.this,ActivityGaleria.class);
                startActivity(intentGaleria);
            }
        });
    }

    private void aumentacontador() {

        // Inicializar el TextView
        textViewCounter = findViewById(R.id.contadorAccesosTxt);

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

