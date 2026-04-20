package com.example.miprimeraapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class lista_amigos extends AppCompatActivity {

    FloatingActionButton fab;

    Bundle parametros = new Bundle();

    DB db;

    ListView ltsAmigos;
    Cursor cAmigos;
    final ArrayList<amigos> alAmigos = new ArrayList<amigos>();
    final ArrayList<amigos> alAmigosCopia = new ArrayList<amigos>();

    JSONArray jsonArray;
    JSONObject jsonObject;

    int posicion = 0;

    amigos misAmigos;

    detectarInternet di;
    obtenerDatosServidor datosServidor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_amigos);

        parametros.putString("accion", "nuevo");
        db = new DB(this);

        fab = findViewById(R.id.fabAgregarAmigos);
        fab.setOnClickListener(v -> abrirActivity());

        obtenerAmigos();
        buscarAmigos();


    }

    // menu desplegable al mantener presionado selecionar en la lista
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        try {
            // Poner info de menu o poner cosas extra en este caso el nombre de quien a
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            posicion = info.position;
            menu.setHeaderTitle(jsonArray.getJSONObject(posicion).getString("nombre"));

        } catch (Exception e) {
             mostrarMsg("Error al mostrar menu: "+ e.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            if(item.getItemId()==R.id.mnxAgregar){
                abrirActivity();
            }else if(item.getItemId()==R.id.mnxModificar){
                parametros.putString("accion", "modificar");
                parametros.putString("amigos", jsonArray.getJSONObject(posicion).toString());
                abrirActivity();
            }else if (item.getItemId()==R.id.mnxEliminar){
                borrarAmigo();
            }
            return true;

        } catch (Exception e) {
            mostrarMsg("Error al selecionar item de menu: "+ e.getMessage());
            return super.onContextItemSelected(item);
        }
    }

    private void borrarAmigo(){
        try{
            String nombre = jsonArray.getJSONObject(posicion).getString("nombre");
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
            confirmacion.setTitle("Esta seguro de borrar a?");
            confirmacion.setMessage(nombre);
            confirmacion.setPositiveButton("SI", (dialog, which)->{
                try{
                    String respuesta = db.administrar_amigos("eliminar",
                            new String[]{jsonArray.getJSONObject(posicion).getString("idAmigo")});
                    if(respuesta.equals("ok")){
                        obtenerAmigos();
                        mostrarMsg("Amigo borrado con exito.");
                    }
                }catch (Exception e){
                    mostrarMsg(e.getMessage());
                }
            });
            confirmacion.setNegativeButton("NO", (dialog, which)->{
                dialog.dismiss();//cerrar el cuadro de dialogo
            });
            confirmacion.create().show();
        } catch (Exception e) {
            mostrarMsg("Error al borrar el amigo: "+ e.getMessage());
        }
    }

    // FUNCION ENCARGADA DE FILTRAR BUSQUEDA
    private void buscarAmigos(){
        TextView tempval = findViewById(R.id.txtBuscarAmigos);
        tempval.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alAmigos.clear();
                String buscar = tempval.getText().toString().trim().toLowerCase();
                // mejor para las busquedas
                if(buscar == ""){
                    alAmigos.addAll(alAmigosCopia);
                }else{
                    for (amigos item:alAmigosCopia){
                        if(item.getNombre().toLowerCase().contains(buscar) ||
                                item.getDui().contains(buscar) ||
                                item.getEmail().toLowerCase().contains(buscar)){
                            alAmigos.add(item);
                        }
                    }
                    ltsAmigos.setAdapter(new AdaptadorAmigos(getApplicationContext(), alAmigos));
                }
            }
        });
    }

    private void abrirActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(parametros);
        startActivity(intent);
    }

    private void obtenerAmigos(){
        try{
            di = new detectarInternet(this);
            if(di.hayConexionInternet()){//si hay conexion a internet
                datosServidor = new obtenerDatosServidor();
                String respuesta = datosServidor.execute().get();
                jsonObject = new JSONObject(respuesta);
                jsonArray = jsonObject.getJSONArray("rows");
                mostrarAmigos();
            }else {//no hay conexion a internet
                cAmigos = db.lista_amigos();
                if (cAmigos.moveToFirst()) {
                    jsonArray = new JSONArray();
                    do {
                        jsonObject = new JSONObject();
                        jsonObject.put("idAmigo", cAmigos.getString(0));
                        jsonObject.put("nombre", cAmigos.getString(1));
                        jsonObject.put("direccion", cAmigos.getString(2));
                        jsonObject.put("telefono", cAmigos.getString(3));
                        jsonObject.put("email", cAmigos.getString(4));
                        jsonObject.put("dui", cAmigos.getString(5));
                        jsonObject.put("foto", cAmigos.getString(6));
                        jsonArray.put(jsonObject);
                    } while (cAmigos.moveToNext());
                    mostrarAmigos();
                } else {
                    mostrarMsg("No hay amigos que mostrar");
                    abrirActivity();
                }
            }
        } catch (Exception e) {
            mostrarMsg(e.getMessage());
        }

    }

    private void mostrarAmigos(){
        try{
            if(jsonArray.length()>0){
                ltsAmigos = findViewById(R.id.ltsAmigos);
                alAmigos.clear();
                alAmigosCopia.clear();

                for(int i=0; i<jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i).getJSONObject("value");
                    misAmigos = new amigos(
                            jsonObject.getString("idAmigo"),
                            jsonObject.getString("nombre"),
                            jsonObject.getString("direccion"),
                            jsonObject.getString("telefono"),
                            jsonObject.getString("email"),
                            jsonObject.getString("dui"),
                            jsonObject.getString("foto")
                    );
                    alAmigos.add(misAmigos);
                }
                alAmigosCopia.addAll(alAmigos);
                ltsAmigos.setAdapter(new AdaptadorAmigos(this, alAmigos));
                registerForContextMenu(ltsAmigos);
            }else {
                mostrarMsg("no hay amigos que mostrar...");
                abrirActivity();
            }
        } catch (Exception e) {
            mostrarMsg(e.getMessage());
        }
    }
    private void mostrarMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}