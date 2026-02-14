package com.example.miprimeraapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;


// Recuerda esto es programando con objetos
public class MainActivity extends AppCompatActivity {

    TextView tempVal;
    Button btn;
    Spinner spn;
    // Matriz dimensional conteniendo las conversiones base
    Double valores[][] = {
            {1.0, 0.85, 7.67, 26.42, 36.80, 495.77}, //moendas
            {1.0, 1000.0, 100.0, 39.3701, 3.280841666667, 1.1963081929167, 1.09361}, //longitud
            {}, //volumen
    };

    String[][] etiquetas = {
            {"Dolar", "Euro", "Quetzal", "Lempira", "Cordoba", "Colon CR"}, //monedas
            {"Mts", "Ml", "Cm", "Pulgada", "Pies", "Vara", "Yarda"}, //Longitud
            {""},  //volumen
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnConvertir);
        btn.setOnClickListener(v->convertir());

        cambiarEtiqueta(0);//valores predeterminaods

        spn= findViewById(R.id.spnTipo);
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                cambiarEtiqueta(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void cambiarEtiqueta(int posicion){
        ArrayAdapter<String>  aaEtiquetas = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                etiquetas[posicion]
        );

        aaEtiquetas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn= findViewById(R.id.spnDe);
        spn.setAdapter(aaEtiquetas);

        spn = findViewById(R.id.spnA);
        spn.setAdapter(aaEtiquetas);



        tempVal = findViewById(R.id.lblRespuesta);
        tempVal.setText("CAMBIO" + posicion);
    }

    private void convertir(){
        // Pociscion selecionada de 0 a 5  del primer spinner
        spn = findViewById(R.id.spnTipo);
        int tipo = spn.getSelectedItemPosition();

        // Pociscion selecionada de 0 a 5  del primer spinner
        spn = findViewById(R.id.spnDe);
        int de = spn.getSelectedItemPosition();

        // Pociscion selecionada de 0 a 5 del segundo spinner
        spn = findViewById(R.id.spnA);
        int a = spn.getSelectedItemPosition();

        // Obtenemos el valor dado en el input/textbox
        tempVal = findViewById(R.id.txtCantidad);
        double cantidad = Double.parseDouble(tempVal.getText().toString());

        // Variable que almacena el calculo de la funcion
        double respuesta = conversor(tipo, de, a, cantidad);

        // Mostrar el resultado en el label
        tempVal = findViewById(R.id.lblRespuesta);
        tempVal.setText("Respuesta: "+ respuesta);
    }


    // Funcion que toma de argumentos a, de y cantidad a convertir devolviendo Double para su uso
    double conversor(int tipo, int de, int a, double cantidad){
        return valores[tipo][a]/valores[tipo][de] * cantidad;
    }

}