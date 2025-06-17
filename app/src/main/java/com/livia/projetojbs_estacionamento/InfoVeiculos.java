package com.livia.projetojbs_estacionamento;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.livia.projetojbs_estacionamento.databinding.ActivityInfoVeiculosBinding;

import java.util.ArrayList;
import java.util.List;

public class InfoVeiculos extends AppCompatActivity {
    private ActivityInfoVeiculosBinding binding;

    private FirebaseFirestore db;

    private List<Veiculo> listaVeiculo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityInfoVeiculosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Configurar o layout do recycleView
        binding.rvUsuario.setLayoutManager(new LinearLayoutManager(this));


        //Configurar o adapter
        VeiculoAdapter veiculoAdapter = new VeiculoAdapter(listaVeiculo, this);
        binding.rvUsuario.setAdapter(veiculoAdapter);


        //Usando o Database
        listar(listaVeiculo, veiculoAdapter, this);
        ImageView btnBuscar = (ImageView) findViewById(R.id.imageView4);
        EditText editTextBusca = binding.inputNomeUsuario;
        TextView txtLimparBusca = binding.textX;
        TextView nomeusuario = binding.textView12;
        //Button bt_registrar_saida = binding.btRegistrarSaida;
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String recebidoNomeUsuario = extras.getString("NOME_USUARIO");

            if (recebidoNomeUsuario != null && !recebidoNomeUsuario.isEmpty()) {
                binding.textView12.setText(recebidoNomeUsuario);
            } else {
                Toast.makeText(this, "Nome de usuário veio vazio no Bundle", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bundle está nulo!", Toast.LENGTH_SHORT).show();
        }

        btnBuscar.setOnClickListener(v -> {
            String termo = editTextBusca.getText().toString().trim();
            if (!termo.isEmpty()) {
                buscar(termo, veiculoAdapter, this);
            } else {
                Toast.makeText(this, "Digite algo para buscar", Toast.LENGTH_SHORT).show();
            }
        });

        txtLimparBusca.setOnClickListener(v -> {
            editTextBusca.setText("");
            listar(listaVeiculo, veiculoAdapter, this);
        });

        Button btnAddVeiculo = findViewById(R.id.add_veiculo);
        btnAddVeiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoVeiculos.this, CadastroPlaca.class);
                startActivity(intent);
            }});
    }
    public void buscar(String termo, VeiculoAdapter adapter, Context c) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Recuperar os dados em TEMPO REAL
        db.collection("veiculo")
                .whereEqualTo("placa", termo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaVeiculo.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Veiculo veiculo = document.toObject(Veiculo.class);
                            listaVeiculo.add(veiculo);
                        }
                        if (listaVeiculo.isEmpty()) {
                            Toast.makeText(this, "Nenhum veículo encontrado com essa placa", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Erro ao buscar veículos", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("veiculo").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(c, " Você está Off-line neste momento..!!", Toast.LENGTH_SHORT).show();
                    System.out.println("Deu ruim " + error.getMessage());
                    return;
                }
            }
        });
    }

    public void listar(List<Veiculo> argLista, VeiculoAdapter adapter, Context c) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Recuperar os dados em TEMPO REAL
        db.collection("veiculo").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(c, "Você está Off-line neste momento..!!", Toast.LENGTH_SHORT).show();
                    System.out.println("Offline... " + error.getMessage());
                    return;
                }

                argLista.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Veiculo objveiculo = doc.toObject(Veiculo.class);
                    argLista.add(objveiculo);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
