package com.example.miprimeraapp;

import android.os.Bundle;
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


// Recuerda esto es programando con objetos
public class MainActivity extends AppCompatActivity {
    TabHost tbh;
    TextView tempVal;
    Button btn;
    Spinner spn;

    // Este seria la conversion de area
    Double longitudes[] = new Double[] {1.0, 10.7639, 1.43115, 1.19599, 0.002288, 0.000143, 1e-4}; //area

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tbh = findViewById(R.id.tbhConversores);
        tbh.setup();

        tbh.addTab(tbh.newTabSpec("Conversor").setContent(R.id.tabLongitud).setIndicator("Conversor", null));
        tbh.addTab(tbh.newTabSpec("Calculo").setContent(R.id.tabCalculo).setIndicator("Calculo", null));


        btn = findViewById(R.id.btnLongitudConvertir);
        btn.setOnClickListener(v->convertirLongitud());

        btn = findViewById(R.id.btnCalculo);
        btn.setOnClickListener(v->calcularTarifa());

    }

    private void convertirLongitud() {
        spn = findViewById(R.id.spnLongitudDe);
        int de = spn.getSelectedItemPosition();

        spn = findViewById(R.id.spnLongitudA);
        int a = spn.getSelectedItemPosition();

        tempVal = findViewById(R.id.txtLongitudCantidad);
        double cantidad = Double.parseDouble(tempVal.getText().toString());
        double respuesta = conversorLongitud(de, a, cantidad);

        tempVal = findViewById(R.id.lblLongitudRespuesta);
        tempVal.setText("Respuesta: " + respuesta);
    }

    double conversorLongitud(int de, int a, double cantidad){
        return longitudes[a]/longitudes[de] * cantidad;
    }

    private void calcularTarifa() {

        tempVal = findViewById(R.id.txtCalculoantidad);

            // Convertir a número
            double metros = Double.parseDouble(tempVal.getText().toString());


            // Calcular tarifa según rangos
            double valorPagar;

            if (metros <= 18) {
                // Cuota fija
                valorPagar = 6.0;
            } else if (metros <= 28) {
                // $6 + $0.45 sobre el exceso de 18
                valorPagar = 6.0 + ((metros - 18) * 0.45);
            } else {
                // metros >= 29
                // $6 + $4.50 (10 metros de 19-28) + $0.65 sobre el exceso de 28
                valorPagar = 6.0 + (10 * 0.45) + ((metros - 28) * 0.65);
            }


            tempVal = findViewById(R.id.lblCalculoRespuesta);
            // Mostrar resultado
            tempVal.setText("Respuesta: "+ valorPagar);
        }



}