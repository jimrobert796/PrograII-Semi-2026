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

import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


// Recuerda esto es programando con objetos
public class MainActivity extends AppCompatActivity {

    DB db;

    Button btn ;

    TextView tempVal;

    String accion="nuevo", idAmigo="", urlFoto, id="", rev="";

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
        fab.setOnClickListener(v->regresarListaAmigos());


        mostrarDatosAmigos();

    }

    private void mostrarDatosAmigos(){
        try{
            Bundle parametros = getIntent().getExtras();
            accion = parametros.getString("accion");
            if(accion.equals("modificar")){
                JSONObject datos = new JSONObject(parametros.getString("amigos"));
                id = datos.getString("_id");
                rev = datos.getString("_rev");
                idAmigo = datos.getString("idAmigo");

                tempVal = findViewById(R.id.txtNombreAmigos);
                tempVal.setText(datos.getString("nombre"));

                tempVal = findViewById(R.id.txtDireccionAmigos);
                tempVal.setText(datos.getString("direccion"));

                tempVal = findViewById(R.id.txtTelefonoAmigos);
                tempVal.setText(datos.getString("telefono"));

                tempVal = findViewById(R.id.txtEmailAmigos);
                tempVal.setText(datos.getString("email"));

                tempVal = findViewById(R.id.txtDuiAmigos);
                tempVal.setText(datos.getString("dui"));

                urlFoto = datos.getString("foto");
                img.setImageURI(Uri.parse(urlFoto));
            }
        }catch (Exception e){
            mostrarMensaje("Error al mostrar los datos: "+ e.getMessage());
        }
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
        try {
            tempVal = findViewById(R.id.txtNombreAmigos);
            String nombre = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDireccionAmigos);
            String direccion = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtTelefonoAmigos);
            String tel = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtEmailAmigos);
            String email = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDuiAmigos);
            String dui = tempVal.getText().toString();

            //guardar datos en la base de datos en local - SQLite
            String[] datos = {idAmigo, nombre, direccion, tel, email, dui, urlFoto};
            db.administrar_amigos(accion, datos);
            //guardar datos en la base de datos CouchDB conWebService y API REST.
            JSONObject datosAmigos = new JSONObject();
            if(accion.equals("modificar")){
                datosAmigos.put("_id", id);
                datosAmigos.put("_rev", rev);
            }
            datosAmigos.put("idAmigo", idAmigo);
            datosAmigos.put("nombre", nombre);
            datosAmigos.put("direccion", direccion);
            datosAmigos.put("telefono", tel);
            datosAmigos.put("email", email);
            datosAmigos.put("dui", dui);
            datosAmigos.put("urlFoto", urlFoto);

            enviarDatosServidor objEnviarDatosServidor = new enviarDatosServidor(this);
            String respuesta = objEnviarDatosServidor.execute(datosAmigos.toString(), "POST", utilidades.url_mantenimiento).get();

            JSONObject respuestaJSON = new JSONObject(respuesta);
            if(respuestaJSON.getBoolean("ok")){
                id = respuestaJSON.getString("id");
                rev = respuestaJSON.getString("rev");
            }else{
                mostrarMensaje("Error: "+ respuestaJSON.getString("msg"));
            }
            mostrarMensaje("Registro de amigo guardado con exito.");
            regresarListaAmigos();
        } catch (Exception e) {
            mostrarMensaje(e.getMessage());
        }
    }

    private void mostrarMensaje(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    private void regresarListaAmigos(){
        Intent intent = new Intent(this, lista_amigos.class);
        startActivity(intent);
    }

}