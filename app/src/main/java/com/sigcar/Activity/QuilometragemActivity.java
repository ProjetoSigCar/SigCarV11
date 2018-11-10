package com.sigcar.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sigcar.Classes.Kilometragem;
import com.sigcar.DAO.ConfiguracaoFirebase;
import com.sigcar.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuilometragemActivity extends AppCompatActivity {

    ListView listViewDetalhes;
    Spinner spinnerVeiculos;
    Spinner spinnerKilometragem;
    DatabaseReference databaseArtists;
    HashMap<String,List<String>> veiculos = null;
    Map<String,List<String>> mapManutecao = null;
    Map<String,List<String>> mapKM = null;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quilometragem);

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();

        listViewDetalhes = (ListView) findViewById(R.id.listViewDetalhes);
        spinnerVeiculos = (Spinner) findViewById(R.id.spinnerVeiculos);
        spinnerKilometragem =(Spinner)  findViewById(R.id.spinnerKilometragem);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("AGUARDE");
        progressDialog.setMessage("Sincronizando as informações...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        firebase.child("Veiculo").getParent().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                veiculos = new  HashMap<String,List<String>> ();
                mapKM = new  HashMap<String,List<String>> ();
                mapManutecao = new HashMap<>();

                List<String>  modelos = new ArrayList<>();

                List<String>  kms = new ArrayList<>();
                List<String>  manutencoes;

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {

                    for(DataSnapshot veiculo : areaSnapshot.getChildren()){

                        modelos.add(veiculo.getKey());

                        for(DataSnapshot quilometragens : veiculo.getChildren()){

                            for(DataSnapshot km : quilometragens.child("quilometragens").getChildren()){

                                kms.add( km.getKey());

                               // Log.d("km",km.getKey());

                                manutencoes = new ArrayList<>();

                                for(DataSnapshot manutencao : km.child("manutencao").getChildren()){

                                   // Log.d("MANUTENCAO",manutencao.getValue().toString());

                                    manutencoes.add(manutencao.getValue().toString());
                                }

                                mapManutecao.put(km.getKey(),manutencoes);

                            }
                            mapKM.put(veiculo.getKey(),kms);
                        }

                    }

                    progressDialog.dismiss();
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(QuilometragemActivity.this, android.R.layout.simple_spinner_item, modelos);

                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerVeiculos.setAdapter(areasAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        spinnerVeiculos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

             String marca = (String) parent.getItemAtPosition(position);

              ArrayAdapter<String>   adapter = new ArrayAdapter<>(QuilometragemActivity.this,
                        android.R.layout.simple_spinner_item, mapKM.get(marca));

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerKilometragem.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerKilometragem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              String kilometragem = (String) parent.getItemAtPosition(position);

               ArrayAdapter<String>   adapter = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_list_item_1, mapManutecao.get(kilometragem));

                listViewDetalhes.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

  /*      listViewArtists.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Artist artist = artistList.get(i);
            new AlertDialog.Builder(this)
                    .setTitle("Delete " + artist.getArtistName() + " "
                    )
                    .setMessage("Você deseja excluir o registro selecionado?")
                    .setPositiveButton("Delete", (dialogInterface, i1) -> {
                        databaseArtists.child(artist.getArtistId()).removeValue();

                        Toast.makeText(this, artist.getArtistName() + ""+" Excluido com Sucesso", Toast.LENGTH_LONG).show();


                    })
                    .setNegativeButton("Cancel", (dialogInterface, i12) -> {
                        dialogInterface.dismiss();
                    })
                    .create()
                    .show();
            return true;
        });
*/
    }

}