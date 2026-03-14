package com.example.miprimeraapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


// Recuerda esto es programando con objetos
public class MainActivity extends AppCompatActivity {

    DB db;

    Button btn ;

    TextView tempval;

    String accion = "Nuevo", idAmigo = "", urlFoto;

    FloatingActionButton fab ;
    ImageView img;

    Intent tomarFotoIntento;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.imgFotoAmigo);
        img.setOnClickListener(v->tomarFoto());

        db = new DB(this);

        btn = findViewById(R.id.btnGuardarAmigo);
        btn.setOnClickListener(v-> guardarAmigo());

        fab = findViewById(R.id.fabListaAmigo);


    }

    private void tomarFoto(){
        tomarFotoIntento = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoAmigo = null;

        try{
            fotoAmigo = crearImgAmigo();
            if(fotoAmigo!=null){
                Uri uriFoto = FileProvider.getUriForFile(MainActivity.this, "com.example.miprimeraapp.fileprovider", fotoAmigo);
                tomarFotoIntento.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                startActivityForResult(tomarFotoIntento, 1);
            }else{
                mostrarMensaje("Nose pudo crear la foto");
            }
        } catch (Exception e) {
            mostrarMensaje("Error al tomar la foto: "+ e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK){
                img.setImageURI(Uri.parse(urlFoto));
            }else{

                mostrarMensaje("Error al mostrar foto");
            }

        } catch (Exception e) {
            mostrarMensaje("Error al abrir camara" + e.getMessage());
        }
    }

    private File crearImgAmigo() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),
                fileMane = "foto_"+ fechaHoraMs;
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if(dirAlmacenamiento.exists()==false){
            dirAlmacenamiento.mkdir();
        }
        File image = File.createTempFile(fileMane, ".jpg", dirAlmacenamiento);
        urlFoto = image.getAbsolutePath();
        return image;
    }

    private void guardarAmigo(){

        tempval = findViewById(R.id.txtNombreAmigos);
        String nombre = tempval.getText().toString();

        tempval = findViewById(R.id.txtDireccionAmigos);
        String direccion = tempval.getText().toString();

        tempval = findViewById(R.id.txtTelefonoAmigos);
        String telefono = tempval.getText().toString();

        tempval = findViewById(R.id.txtEmailAmigos);
        String email = tempval.getText().toString();

        tempval = findViewById(R.id.txtDuiAmigos);
        String dui = tempval.getText().toString();

        String[] datos = {idAmigo, nombre,direccion, telefono, email, dui, urlFoto};

        db.administrar_amigos(accion, datos);
        mostrarMensaje("Registo de amigo guardado con exito.");


    }

    private void mostrarMensaje(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}