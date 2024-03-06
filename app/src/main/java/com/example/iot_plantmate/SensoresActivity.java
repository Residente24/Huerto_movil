package com.example.iot_plantmate;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SensoresActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // El usuario ha iniciado sesión
            cargarDatosCultivo(currentUser.getUid());
        } else {
            // El usuario no ha iniciado sesión, manejar según tus necesidades
        }

        // Configurar el ExpandableListView y el adaptador
        expandableListView = findViewById(R.id.expandableListView);
        expandableListAdapter = new ExpandableListAdapter();
        expandableListView.setAdapter(expandableListAdapter);
    }

    private void cargarDatosCultivo(String userId) {
        mDatabase.child(userId).child("cultivos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listDataHeader = new ArrayList<>();
                listDataChild = new HashMap<>();

                // Iterar a través de los registros del usuario
                for (DataSnapshot cultivoSnapshot : dataSnapshot.getChildren()) {
                    // Obtener el nombre del cultivo (grupo)
                    String nombreCultivo = ("Cultivo: " + cultivoSnapshot.child("nombreComun").getValue(String.class));
                    listDataHeader.add(nombreCultivo);

                    // Obtener detalles del cultivo (niño)
                    List<String> detallesCultivo = new ArrayList<>();
                    detallesCultivo.add("ID: " + cultivoSnapshot.child("id").getValue(String.class));
                    detallesCultivo.add("Temperatura: " + cultivoSnapshot.child("temperaturaC").getValue(Float.class));
                    detallesCultivo.add("ph: " + cultivoSnapshot.child("pH").getValue(Float.class));
                    detallesCultivo.add("Humedad del suelo: " + cultivoSnapshot.child("humedadSueloPorcentual").getValue(Float.class));
                    detallesCultivo.add("Humedad del aire: " + cultivoSnapshot.child("humedadAmbientePorcentual").getValue(Float.class));
                    // ... repite para otros campos

                    listDataChild.put(nombreCultivo, detallesCultivo);
                }

                // Notificar al adaptador sobre los cambios en los datos
                expandableListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error de la base de datos según tus necesidades
            }
        });
    }

    // Adaptador personalizado para ExpandableListView
    class ExpandableListAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return listDataHeader != null ? listDataHeader.size() : 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return listDataChild != null && listDataHeader != null ?
                    listDataChild.get(listDataHeader.get(groupPosition)).size() : 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return listDataHeader != null ? listDataHeader.get(groupPosition) : null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return listDataChild != null && listDataHeader != null ?
                    listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition) : null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            }
            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText((String) getGroup(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            }
            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText((String) getChild(groupPosition, childPosition));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}