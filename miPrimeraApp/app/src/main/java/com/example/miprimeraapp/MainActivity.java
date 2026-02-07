package com.example.miprimeraapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


/*
Tarea para viernes 06/02/2026 individual.
Factorial
Porcentaje
Exponenciacion
Raiz
* */

// Recuerda esto es programando con objetos
public class MainActivity extends AppCompatActivity {

    //Declaracion de variables
    TextView tempVal; // tempval es un TextViews (campo de texto)
    Button btn; // btn es un Button

    Spinner spn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Enlazar las variables con los elementos XML
        btn = findViewById(R.id.btnCalcular);

        // Añadir eventos a las variables, al presionar el boton hace la funcion
        btn.setOnClickListener(v -> calcular());
    }

    private void calcular(){
        // Usamos tempval para obtener el valor no para almacenar
        tempVal = findViewById(R.id.txtNum1);
        // almacenamos  conertumos a double (obtenemos el texto del objeto y se convierte a string)
        double num1 =  Double.parseDouble(tempVal.getText().toString());

        tempVal = findViewById(R.id.txtNum2);
        double num2 = Double.parseDouble(tempVal.getText().toString());

        double respuesta = 0;


        spn = findViewById(R.id.cboOpciones);
        switch (spn.getSelectedItemPosition()){
            case 0: // Posicion 0 -> SUMA
                respuesta = num1 + num2;
                break;
            case 1:// Posicion 1 -> RESTA
                respuesta = num1 - num2;
                break;
            case 2:// Posicion 2 -> MULTI
                respuesta = num1 * num2;
                break;
            case  3:// Posicion 3 -> DIVISION
                respuesta = num1 / num2;
                break;
            case 4:
                double facto = 1;
                for (int i = 1; i <= num1; i++) {
                    facto = facto * i;
                }
                respuesta = facto;
                break;
            case 5:
                // Porcentaje de numero  20% de 200 -> 20*200 / 100 = 40
                respuesta = (num1 * num2)/ 100;
                break;
            case 6:
                respuesta = Math.pow(num1,num2);
                break;
            case 7:
                respuesta = Math.sqrt(num1);
                break;
        }



        tempVal = findViewById(R.id.lblRespuesta);
        tempVal.setText("Respuesta: "+ respuesta);

    }
}