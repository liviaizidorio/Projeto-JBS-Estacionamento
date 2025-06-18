package com.livia.projetojbs_estacionamento;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    private VeiculoAdapter veiculoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoVeiculosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        binding.rvUsuario.setLayoutManager(new LinearLayoutManager(this));
        veiculoAdapter = new VeiculoAdapter(listaVeiculo, this);
        binding.rvUsuario.setAdapter(veiculoAdapter);

        // Nome do usuário vindo da tela anterior
        TextView txtNome = findViewById(R.id.textView90);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String recebidoNomeUsuario = extras.getString("NOME_USUARIO");
            if (recebidoNomeUsuario != null && !recebidoNomeUsuario.isEmpty()) {
                txtNome.setText(recebidoNomeUsuario);
            } else {
                Toast.makeText(this, "Nome de usuário veio vazio no Bundle", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bundle está nulo!", Toast.LENGTH_SHORT).show();
        }

        // Carregar todos os veículos ao abrir a tela
        listar();

        ImageView btnBuscar = findViewById(R.id.imageView4);
        EditText editTextBusca = binding.inputNomeUsuario;
        TextView txtLimparBusca = binding.textX;

        editTextBusca.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String termo = s.toString().trim();
                veiculoAdapter.setHighlightText(termo);
                veiculoAdapter.notifyDataSetChanged();
            }
        });

        btnBuscar.setOnClickListener(v -> {
            String termo = editTextBusca.getText().toString().trim();
            if (!termo.isEmpty()) {
                buscar(termo);
                veiculoAdapter.setHighlightText("");
            }
        });

        txtLimparBusca.setOnClickListener(v -> {
            editTextBusca.setText("");
            listar();
        });

        // Busca em tempo real enquanto digita
        binding.inputNomeUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarPorTrecho(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Limpar busca
        binding.textX.setOnClickListener(v -> {
            binding.inputNomeUsuario.setText("");
            listar();
        });

        // Voltar
        ImageView imgVoltar = findViewById(R.id.imgVoltar);
        imgVoltar.setOnClickListener(v -> {
            Intent rota = new Intent(this, Cadastro.class);
            startActivity(rota);
        });

        // Ir para cadastrar novo veículo
        Button btnAddVeiculo = findViewById(R.id.add_veiculo);
        btnAddVeiculo.setOnClickListener(v -> {
            Intent intent = new Intent(InfoVeiculos.this, CadastroPlaca.class);
            startActivity(intent);
        });
    }

    public void buscar(String termo) {
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
                            Toast.makeText(this, "Nenhum veículo encontrado", Toast.LENGTH_SHORT).show();
                        }
                        veiculoAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void buscarPorTrecho(String termo) {
        if (termo.isEmpty()) {
            listar();
            return;
        }

        String fimTermo = termo + "\uf8ff";  // Para buscar prefixo no Firestore

        db.collection("veiculo")
                .whereGreaterThanOrEqualTo("placa", termo)
                .whereLessThanOrEqualTo("placa", fimTermo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaVeiculo.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Veiculo veiculo = doc.toObject(Veiculo.class);
                        listaVeiculo.add(veiculo);
                    }
                    veiculoAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro na busca", Toast.LENGTH_SHORT).show());
    }

    private void listar() {
        db.collection("veiculo").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(InfoVeiculos.this, "Você está off-line!", Toast.LENGTH_SHORT).show();
                    System.out.println("Erro Firestore: " + error.getMessage());
                    return;
                }

                listaVeiculo.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Veiculo veiculo = doc.toObject(Veiculo.class);
                    listaVeiculo.add(veiculo);
                }
                veiculoAdapter.notifyDataSetChanged();
            }
        });
    }
}



// ---- Classe SimpleTextWatcher ----
abstract class SimpleTextWatcher implements android.text.TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(android.text.Editable s) {}
}