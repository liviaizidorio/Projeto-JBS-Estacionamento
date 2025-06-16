package com.livia.projetojbs_estacionamento;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.livia.projetojbs_estacionamento.databinding.ActivityListaCarrosBinding;

import org.checkerframework.checker.units.qual.A;

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

        listaVeiculos = new ArrayList<>();
        adapter = new VeiculoAdapter(listaVeiculos, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        imgApagarRegistros = findViewById(R.id.img_apagar_regristros);

        imgApagarRegistros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPopupLimparRegistros();
            }
        });
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
        db = FirebaseFirestore.getInstance();
        carregarTodosOsVeiculos();
        imageBuscar.setOnClickListener(v -> {
            String termo = editTextPlaca.getText().toString().trim();
            if (!termo.isEmpty()) {
                buscarVeiculoPorPlaca();
            } else {
                Toast.makeText(this, "Digite algo para buscar", Toast.LENGTH_SHORT).show();
            }
        });

        txtLimparBusca.setOnClickListener(v -> {
            editTextPlaca.setText("");
            carregarTodosOsVeiculos();
        });
        imageBuscar.setOnClickListener(v -> buscarVeiculoPorPlaca());
    }
    private void mostrarPopupLimparRegistros() {

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

    private void buscarVeiculoPorPlaca() {
        String placaBuscada = editTextPlaca.getText().toString().trim();

        if (placaBuscada.isEmpty()) {
            Toast.makeText(this, "Digite uma placa para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("veiculo")
                .whereEqualTo("placa", placaBuscada)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaVeiculos.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Veiculo veiculo = document.toObject(Veiculo.class);
                            listaVeiculos.add(veiculo);
                        }
                        if (listaVeiculos.isEmpty()) {
                            Toast.makeText(this, "Nenhum veículo encontrado com essa placa", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Erro ao buscar veículos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
