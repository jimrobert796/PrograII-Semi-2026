package com.example.miprimeraapp;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;

// Recuerda esto es programando con objetos
public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    Button btn;
    TextView tempVal;
    String accion="", idAmigo="", id="", rev="";
    ImageView img;
    String urlCompletaFoto="", getUrlCompletaFotoFireStore="";
    Intent tomarFotoIntent;
    detectarInternet di;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}