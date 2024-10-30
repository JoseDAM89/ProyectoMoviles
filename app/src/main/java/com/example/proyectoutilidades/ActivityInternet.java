package com.example.proyectoutilidades;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityInternet extends AppCompatActivity {

    private final String ciudad = "Madrid";

    private String solucion;

    private TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityinternet);  // Asegúrate de incluir la vista
        t1 = findViewById(R.id.t1);
    }

    private void obtenerDatosCiudad(String ciudad) {
        new Thread(() -> {
            try {
                String sparqlQuery = String.format(
                        "SELECT ?city ?population ?latitude ?longitude WHERE {" +
                                "?city wdt:P31 wd:Q515; " +
                                "rdfs:label \"%s\"@es; " +
                                "wdt:P1082 ?population; " +
                                "wdt:P625 ?location. " +
                                "BIND(geof:latitude(?location) AS ?latitude). " +
                                "BIND(geof:longitude(?location) AS ?longitude)." +
                                "}", ciudad);

                String urlStr = "https://query.wikidata.org/sparql?query=" +
                        Uri.encode(sparqlQuery) +
                        "&format=json";

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                solucion = analizarRespuesta(response.toString());

                new Handler(Looper.getMainLooper()).post(() -> t1.setText(solucion));


            } catch (Exception e) {
                Log.e("Error", "Error en la consulta a Wikidata", e);
            }
        }).start();
    }

    private String analizarRespuesta(String jsonResponse) {
        StringBuilder result = new StringBuilder();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray results = jsonObject.getJSONObject("results").getJSONArray("bindings");
            if (results.length() > 0) {
                String population = results.getJSONObject(0).getJSONObject("population").getString("value");
                String latitude = results.getJSONObject(0).getJSONObject("latitude").getString("value");
                String longitude = results.getJSONObject(0).getJSONObject("longitude").getString("value");

                result.append("Población: ").append(population).append("\n");
                result.append("Latitud: ").append(latitude).append("\n");
                result.append("Longitud: ").append(longitude).append("\n");
            } else {
                result.append("No se encontraron resultados para la ciudad.");
            }
        } catch (JSONException e) {
            Log.e("Error", "Error al analizar la respuesta JSON", e);
            result.append("Error al analizar la respuesta.");
        }
        return result.toString();
    }
}
