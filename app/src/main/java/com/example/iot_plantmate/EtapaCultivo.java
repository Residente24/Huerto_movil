package com.example.iot_plantmate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EtapaCultivo extends AppCompatActivity {

    TextView textViewFechaEtapa;
    EditText editTextIdcultivo, editTextNombreEtapa, editTextObservaciones, editTextMedidas,
            editTextHorasCalor, editTextTierraEtapa, editTextUbicacionEtapa,
            editTextSubstratoEtapa;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageViewSelected;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private String idEtapa;  // Nueva variable para almacenar el ID de la etapa

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etapa_cultivo);
        imageViewSelected = findViewById(R.id.imageViewSelected);

        textViewFechaEtapa = findViewById(R.id.textViewFechaEtapa);
        editTextIdcultivo = findViewById(R.id.editTextIdcultivo);
        editTextNombreEtapa = findViewById(R.id.editTextNombreEtapa);
        editTextObservaciones = findViewById(R.id.editTextObservaciones);
        editTextMedidas = findViewById(R.id.editTextMedidas);
        editTextHorasCalor = findViewById(R.id.editTextHorascalor);
        editTextTierraEtapa = findViewById(R.id.editTextTierraEtapa);
        editTextUbicacionEtapa = findViewById(R.id.editTextUbicacionEtapa);
        editTextSubstratoEtapa = findViewById(R.id.editTextSubstratoEtapa);

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // Inicializar Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();
        // Inicializar la referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Obtener el ID del usuario actual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        // Configurar el OnClickListener para el botón de registro
        Button btnRegistrarEtapa = findViewById(R.id.btnRegistrarEtapa);
        btnRegistrarEtapa.setOnClickListener(v -> registrarEtapa());
        // Configurar el OnClickListener para el botón de subir imagen
        Button buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
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
                        textViewFechaEtapa.setText(fechaSeleccionada);
                    }
                },
                anio, mes, dia);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri selectedImageUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewSelected.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void dispatchTakePictureIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        takePictureLauncher.launch(pickImageIntent);
    }

    private void registrarEtapa() {
        // Obtén los valores de las vistas
        String idcultivo = editTextIdcultivo.getText().toString().trim();
        String nombreEtapa = editTextNombreEtapa.getText().toString().trim();
        String observaciones = editTextObservaciones.getText().toString().trim();
        String medidas = editTextMedidas.getText().toString().trim();
        String horasCalor = editTextHorasCalor.getText().toString().trim();
        String tierra = editTextTierraEtapa.getText().toString().trim();
        String ubicacion = editTextUbicacionEtapa.getText().toString().trim();
        String substrato = editTextSubstratoEtapa.getText().toString().trim();

        String fecha = textViewFechaEtapa.getText().toString().trim();

        // Verifica si los campos están vacíos
        if (nombreEtapa.isEmpty() || observaciones.isEmpty() || medidas.isEmpty() ||
                horasCalor.isEmpty() || tierra.isEmpty() || ubicacion.isEmpty() ||
                substrato.isEmpty() || fecha.isEmpty()) {
            // Muestra un mensaje de error
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            // Crea un nuevo objeto Etapa con el ID del usuario
            Etapa etapa = new Etapa(idcultivo,nombreEtapa, observaciones, medidas, horasCalor,
                    tierra, ubicacion, substrato, fecha);

            // Genera un nuevo ID para la etapa
            idEtapa = databaseReference.child("users").child(userId).child("etapas").push().getKey();

            // Asigna el ID al objeto Etapa
            etapa.setId(idEtapa);

            // Subir la imagen a Firebase Storage
            uploadImageToFirebaseStorage(idEtapa, userId, etapa);

            // Muestra un mensaje de éxito
            Toast.makeText(this, "Etapa registrada con éxito", Toast.LENGTH_SHORT).show();
            // Reinicia los campos del formulario
            limpiarCampos();
        }
    }

    // Método para limpiar los campos del formulario
    private void limpiarCampos() {
        editTextIdcultivo.setText("");
        editTextNombreEtapa.setText("");
        editTextObservaciones.setText("");
        editTextMedidas.setText("");
        editTextHorasCalor.setText("");
        editTextTierraEtapa.setText("");
        editTextUbicacionEtapa.setText("");
        editTextSubstratoEtapa.setText("");
        textViewFechaEtapa.setText("Fecha: No Seleccionada");
    }

    private void uploadImageToFirebaseStorage(String etapaId, String userId, Etapa etapa) {
        // Obtener la referencia al Storage con el nombre de la imagen
        StorageReference imageRef = storageReference.child("users/" + userId +"/" + "etapas/" + etapaId + ".jpg");

        // Obtener el bitmap de la imagen desde el ImageView
        imageViewSelected.setDrawingCacheEnabled(true);
        imageViewSelected.buildDrawingCache();
        Bitmap bitmap = imageViewSelected.getDrawingCache();

        // Convertir el bitmap a bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Subir la imagen al Storage
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Imagen subida con éxito
            // Obtener la URL de la imagen subida
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imagenUrl = uri.toString();
                // Guardar la URL de la imagen en la base de datos
                etapa.setImageUrl(imagenUrl);
                databaseReference.child("users").child(userId).child("etapas").child(etapaId).setValue(etapa);
            });

            // Reiniciar el campo de la imagen
            imageViewSelected.setImageBitmap(null);
        }).addOnFailureListener(e -> {
            // Error al subir la imagen
            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
        });
    }

    // Clase interna para el modelo Etapa
    private static class Etapa {
        public String id;
        public String nombreEtapa, observaciones, medidas, horasCalor, tierra,
                ubicacion, substrato, fecha, idcultivo;
        public String imagenUrl; // Nuevo campo para almacenar la URL de la imagen

        public Etapa() {
            // Constructor vacío necesario para Firebase
        }

        public Etapa(String idcultivo,String nombreEtapa, String observaciones, String medidas, String horasCalor,
                     String tierra, String ubicacion, String substrato, String fecha) {
            this.idcultivo =idcultivo;
            this.nombreEtapa = nombreEtapa;
            this.observaciones = observaciones;
            this.medidas = medidas;
            this.horasCalor = horasCalor;
            this.tierra = tierra;
            this.ubicacion = ubicacion;
            this.substrato = substrato;
            this.fecha = fecha;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setImageUrl(String imagenUrl) {
            this.imagenUrl = imagenUrl;
        }
    }
}