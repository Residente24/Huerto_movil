package com.example.iot_plantmate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Random;
import java.util.Calendar;

public class NuevoCultivoActivity extends AppCompatActivity {

    TextView textViewFecha;
    EditText editTextNombreComun, editTextNombreCientifico, editTextDescripcion,
            editTextCrecimiento, editTextLuz, editTextTierra, editTextUbicacion,
            editTextSubstrato;

    private FirebaseAuth firebaseAuth;
    private String userId;
    private String idCultivo;  // Nueva variable para almacenar el ID del cultivo

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cultivo);

        textViewFecha = findViewById(R.id.textViewFecha);
        editTextNombreComun = findViewById(R.id.editTextNombreComun);
        editTextNombreCientifico = findViewById(R.id.editTextNombreCientifico);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextCrecimiento = findViewById(R.id.editTextCrecimiento);
        editTextLuz = findViewById(R.id.editTextLuz);
        editTextTierra = findViewById(R.id.editTextTierra);
        editTextUbicacion = findViewById(R.id.editTextUbicacion);
        editTextSubstrato = findViewById(R.id.editTextSubstrato);

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Obtener el ID del usuario actual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        // Configurar el OnClickListener para el botón de registro
        Button btnRegistrarCultivo = findViewById(R.id.btnRegistrarCultivo);
        btnRegistrarCultivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarCultivo();
            }
        });
    }

    public void mostrarDatePicker(View view) {
        Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Actualizar el TextView con la fecha seleccionada
                        String fechaSeleccionada = day + "/" + (month + 1) + "/" + year;
                        textViewFecha.setText(fechaSeleccionada);
                    }
                },
                anio, mes, dia);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private void registrarCultivo() {
        // Obtén los valores de las vistas
        String nombreComun = editTextNombreComun.getText().toString().trim();
        String nombreCientifico = editTextNombreCientifico.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String crecimiento = editTextCrecimiento.getText().toString().trim();
        String luzt = editTextLuz.getText().toString().trim();
        String tierra = editTextTierra.getText().toString().trim();
        String ubicacion = editTextUbicacion.getText().toString().trim();
        String substrato = editTextSubstrato.getText().toString().trim();
        String fecha = textViewFecha.getText().toString().trim();

        // Verifica si los campos están vacíos
        if (nombreComun.isEmpty() || nombreCientifico.isEmpty() || descripcion.isEmpty() ||
                crecimiento.isEmpty() || luzt.isEmpty() || tierra.isEmpty() || ubicacion.isEmpty() ||
                substrato.isEmpty() || fecha.isEmpty()) {
            // Muestra un mensaje de error
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            // Crea un nuevo objeto Cultivo con el ID del usuario
            Cultivo cultivo = new Cultivo(nombreComun, nombreCientifico, descripcion, crecimiento,
                    luzt, tierra, ubicacion, substrato, fecha);

            // Obtén una referencia a la base de datos de Firebase
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(userId).child("cultivos");

            // Genera un nuevo ID para el cultivo
            idCultivo = databaseReference.push().getKey();

            // Genera un nuevo ID para la etapa
            idCultivo = generateRandomId("cult", 6); // "etp" seguido de 6 dígitos aleatorios

            // Asigna el ID al objeto Cultivo
            cultivo.setId(idCultivo);

            // Guarda el cultivo en la base de datos usando el ID generado
            databaseReference.child(idCultivo).setValue(cultivo);

            // Muestra un mensaje de éxito
            Toast.makeText(this, "Cultivo registrado con éxito", Toast.LENGTH_SHORT).show();
            // Reinicia los campos del formulario
            limpiarCamposCultivo();
        }
    }
    // Método para limpiar los campos del formulario de cultivo
    private void limpiarCamposCultivo() {
        editTextNombreComun.setText("");
        editTextNombreCientifico.setText("");
        editTextDescripcion.setText("");
        editTextCrecimiento.setText("");
        editTextLuz.setText("");
        editTextTierra.setText("");
        editTextUbicacion.setText("");
        editTextSubstrato.setText("");
        textViewFecha.setText("Fecha: No Seleccionada");
    }
    // Método para generar un ID aleatorio
    private String generateRandomId(String prefix, int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(prefix);
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10)); // Agrega un dígito aleatorio (0-9)
        }
        return builder.toString();
    }

    // Clase interna para el modelo Cultivo
    private static class Cultivo {
        public String id;
        public String nombreComun, nombreCientifico, descripcion, crecimiento, luzt, tierra,
                ubicacion, substrato, fecha;

        public Cultivo() {
            // Constructor vacío necesario para Firebase
        }

        public Cultivo(String nombreComun, String nombreCientifico, String descripcion,
                       String crecimiento, String luzt, String tierra, String ubicacion,
                       String substrato, String fecha) {
            this.nombreComun = nombreComun;
            this.nombreCientifico = nombreCientifico;
            this.descripcion = descripcion;
            this.crecimiento = crecimiento;
            this.luzt = luzt;
            this.tierra = tierra;
            this.ubicacion = ubicacion;
            this.substrato = substrato;
            this.fecha = fecha;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}