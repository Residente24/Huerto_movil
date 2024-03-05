package com.example.iot_plantmate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilActivity extends AppCompatActivity {


    private TextView nombreTextView, emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        nombreTextView = findViewById(R.id.textViewName);
        emailTextView = findViewById(R.id.textViewEmail);

        // Obtén la instancia actual del usuario de Firebase
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        // Obtén la referencia a la base de datos de Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Obtén la referencia específica del usuario actual
        DatabaseReference usuarioReference = databaseReference.child(usuario.getUid());

        // Escucha los cambios en los datos del usuario
        usuarioReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Recupera los datos del usuario
                String nombre = dataSnapshot.child("nombre").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);

                // Muestra los datos en los TextView
                nombreTextView.setText(nombre);
                emailTextView.setText(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Maneja errores de lectura de datos
            }
        });
    }
}