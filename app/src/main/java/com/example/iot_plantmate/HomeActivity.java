package com.example.iot_plantmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*Logica card de cerrar sesión*/
        CardView exit = findViewById(R.id.CerrarSesion);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                // Después de cerrar sesión, inicia LoginActivity
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Limpiar la pila de actividades
                startActivity(intent);
                //startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

        /*Logica card nuevo cultivo*/
        CardView nuevo = findViewById(R.id.NuevoCultivo);
        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this,NuevoCultivoActivity.class));
            }
        });

        /*Logica card etapa cultivo*/
        CardView Etapa = findViewById(R.id.RegistroEtapa);
        Etapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,EtapaCultivo.class));
            }
        });

        /*Logica card Tutoriales*/
        CardView Tutoriales = findViewById(R.id.Tutoriales);
        Tutoriales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,Tutoriales.class));
            }
        });

        /*Logica card Perfil*/
        CardView Perfil = findViewById(R.id.Perfil);
        Perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,PerfilActivity.class));
            }
        });

        /*Logica card Sensores*/
        CardView Sensores = findViewById(R.id.Sensores);
        Sensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,SensoresActivity.class));
            }
        });

    }
}