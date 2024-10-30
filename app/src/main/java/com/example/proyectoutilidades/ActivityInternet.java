package com.example.proyectoutilidades;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

public class ActivityInternet extends AppCompatActivity {
    private EditText ciudadEditText;
    private TextView resultadoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityinternet);

        ciudadEditText = findViewById(R.id.editTextText);
        resultadoTextView = findViewById(R.id.t1); //Poner aqui el textview
        Button buttonBuscar = findViewById(R.id.buttonBuscar);

        buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ciudad = ciudadEditText.getText().toString();
                if (!ciudad.isEmpty()) {
                    new BuscarDatosCiudad().execute(ciudad);
                } else {
                    resultadoTextView.setText("Por favor, ingrese un nombre de ciudad.");
                }
            }
        });
    }

    private class BuscarDatosCiudad extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String ciudad = params[0];
            try {
                // Construir la consulta SPARQL con el nombre de la ciudad ingresada por el usuario
                String preguntaSQL = "SELECT ?city ?population ?latitude ?longitude WHERE { " +
                        "?city wdt:P31 wd:Q515; " +
                        "rdfs:label \"" + ciudad + "\"@es; " +
                        "wdt:P1082 ?population; " +
                        "wdt:P625 ?location. " +
                        "BIND(geof:latitude(?location) AS ?latitude). " +
                        "BIND(geof:longitude(?location) AS ?longitude). }";

                // Codificar la consulta SPARQL para enviarla en la URL
                String encoder = URLEncoder.encode(preguntaSQL, "UTF-8");
                String wiki = "https://query.wikidata.org/sparql?query=" + encoder;

                // Configurar la conexión HTTP
                URL url = new URL(wiki);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setRequestMethod("GET");
                conexion.setRequestProperty("Accept", "application/json");

                // Leer la respuesta
                BufferedReader leer = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = leer.readLine()) != null) {
                    respuesta.append(linea);
                }
                leer.close();

                // Procesar el JSON de respuesta
                JSONObject jsonRespuesta = new JSONObject(respuesta.toString());
                JSONArray resultados = jsonRespuesta.getJSONObject("results").getJSONArray("bindings");
                if (resultados.length() > 0) {
                    JSONObject infociudad = resultados.getJSONObject(0);
                    String poblacion = infociudad.getJSONObject("population").getString("value");
                    String latitud = infociudad.getJSONObject("latitude").getString("value");
                    String longitud = infociudad.getJSONObject("longitude").getString("value");

                    return "Ciudad: " + ciudad + "\nPoblación: " + poblacion +
                            "\nLatitud: " + latitud + "\nLongitud: " + longitud;
                } else {
                    return "No se encontró información para la ciudad: " + ciudad;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error al realizar la consulta.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            resultadoTextView.setText(result);
        }
    }
}
