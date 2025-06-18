package com.livia.projetojbs_estacionamento;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.livia.projetojbs_estacionamento.databinding.ActivityListaCarrosBinding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ListaCarros extends AppCompatActivity {
    private ActivityListaCarrosBinding binding;

    private EditText editTextPlaca;
    private ImageView imageBuscar;
    private TextView txtLimparBusca;
    private ImageView imgApagarRegistros;

    private RecyclerView recyclerView;
    private VeiculoAdapter adapter;
    private List<Veiculo> listaVeiculos;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaCarrosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editTextPlaca = findViewById(R.id.input_nome_usuario);
        imageBuscar = findViewById(R.id.imageView4);
        recyclerView = findViewById(R.id.rvAdmin);
        txtLimparBusca = findViewById(R.id.textView9);
        imgApagarRegistros = findViewById(R.id.img_apagar_regristros);

        listaVeiculos = new ArrayList<>();
        adapter = new VeiculoAdapter(listaVeiculos, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        imgApagarRegistros.setOnClickListener(v -> mostrarPopupLimparRegistros());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String recebidoNomeUsuario = extras.getString("NOME_USUARIO");
            if (recebidoNomeUsuario != null && !recebidoNomeUsuario.isEmpty()) {
                binding.textView90.setText(recebidoNomeUsuario);
            } else {
                Toast.makeText(this, "Nome de usuário veio vazio no Bundle", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bundle está nulo!", Toast.LENGTH_SHORT).show();
        }

        carregarTodosOsVeiculos();

        // Buscar ao digitar
        editTextPlaca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarVeiculosPorTrechoPlaca(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        txtLimparBusca.setOnClickListener(v -> {
            editTextPlaca.setText("");
            carregarTodosOsVeiculos();
        });

        ImageView imgVoltar = findViewById(R.id.imgVoltar);
        imgVoltar.setOnClickListener(v -> {
            Intent rota = new Intent(this, Cadastro.class);
            startActivity(rota);
        });




        ImageView btnBuscar = findViewById(R.id.imageView4);
        EditText editTextBusca = binding.inputNomeUsuario;
        TextView txtLimparBusca = binding.textView9;

        editTextBusca.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String termo = s.toString().trim();
                adapter.setHighlightText(termo);
                adapter.notifyDataSetChanged();
            }
        });

        btnBuscar.setOnClickListener(v -> {
            String termo = editTextBusca.getText().toString().trim();
            if (!termo.isEmpty()) {
                buscar(termo);
                adapter.setHighlightText("");
            }
        });

        txtLimparBusca.setOnClickListener(v -> {
            editTextBusca.setText("");
            listar();
        });
    }

    public void buscar(String termo) {
        db.collection("veiculo")
                .whereEqualTo("placa", termo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaVeiculos.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Veiculo veiculo = document.toObject(Veiculo.class);
                            listaVeiculos.add(veiculo);
                        }
                        if (listaVeiculos.isEmpty()) {
                            Toast.makeText(this, "Nenhum veículo encontrado", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void listar() {
        db.collection("veiculo").addSnapshotListener((value, error) -> {
            if (error != null) return;
            listaVeiculos.clear();
            for (DocumentSnapshot doc : value.getDocuments()) {
                Veiculo objveiculo = doc.toObject(Veiculo.class);
                listaVeiculos.add(objveiculo);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void buscarVeiculosPorTrechoPlaca(String termo) {
        if (termo.isEmpty()) {
            carregarTodosOsVeiculos();
            return;
        }

        String fimTermo = termo + "\uf8ff";  // Truque para buscar tudo que começa com o termo

        db.collection("veiculo")
                .whereGreaterThanOrEqualTo("placa", termo)
                .whereLessThanOrEqualTo("placa", fimTermo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaVeiculos.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Veiculo veiculo = document.toObject(Veiculo.class);
                        listaVeiculos.add(veiculo);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao buscar veículos", Toast.LENGTH_SHORT).show();
                });
    }

    private void carregarTodosOsVeiculos() {
        db.collection("veiculo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaVeiculos.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Veiculo veiculo = document.toObject(Veiculo.class);
                            listaVeiculos.add(veiculo);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Erro ao carregar veículos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarPopupLimparRegistros() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmar_exclusao);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextInputEditText inputSenhaAdmin = dialog.findViewById(R.id.inputPlaca);

        dialog.findViewById(R.id.fecharCard).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.botaoRegistrar).setOnClickListener(v1 -> {
            db.collection("admin")
                    .whereEqualTo("senha", inputSenhaAdmin.getText().toString().trim())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> db.collection("veiculo")
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot doc : querySnapshot) {
                                    Veiculo veiculo = doc.toObject(Veiculo.class);
                                    LocalDateTime atual = LocalDateTime.now().minusMonths(1);
                                    LocalDateTime dtVeiculo = LocalDateTime.parse(veiculo.getEntradaDia() + "T" + veiculo.getEntradaHora());
                                    if (dtVeiculo.isBefore(atual) || dtVeiculo.isEqual(atual)) {
                                        db.collection("veiculo")
                                                .document(veiculo.getPlaca())
                                                .delete();
                                    }
                                }
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Veículos excluídos com sucesso", Toast.LENGTH_SHORT).show();
                                carregarTodosOsVeiculos();
                            }))
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Senha incorreta", Toast.LENGTH_SHORT).show());
        });

        dialog.show();
    }
}


// ---- Classe SimpleTextWatcher ----
abstract class SimpleTextWatcherListar implements android.text.TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(android.text.Editable s) {}
}