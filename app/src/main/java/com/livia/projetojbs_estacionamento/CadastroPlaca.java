package com.livia.projetojbs_estacionamento;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.livia.projetojbs_estacionamento.databinding.ActivityCadastroPlacaBinding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CadastroPlaca extends AppCompatActivity {
    private ActivityCadastroPlacaBinding binding;

    private List<Veiculo> listaVeiculo = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_placa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        LocalDateTime agora = LocalDateTime.now();
        VeiculoAdapter veiculoAdapter = new VeiculoAdapter(listaVeiculo, this);



        binding = ActivityCadastroPlacaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EditText editTextPlaca = binding.inputNomeUsuario;
        Button bt_enviar_login = binding.btEnviarLogin;
        String placa = editTextPlaca.getText().toString().trim();

        LocalDate dataAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String horaFormatada = horaAtual.format(formatter);


        int result = verificarExistencia(placa);
        if (result == 1) {
            bt_enviar_login.setOnClickListener(v -> {
                String placaDigitada = editTextPlaca.getText().toString().trim();
                if (!placaDigitada.isEmpty()) {

                    Veiculo veiculo = new Veiculo(placaDigitada, dataAtual.toString(), horaFormatada, null, null);
                    salvar(veiculo, this);
                } else {
                    Toast.makeText(this, "Digite algo para prosseguir", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {
            Toast.makeText(this, "Veículo já existe!", Toast.LENGTH_SHORT).show();
        }

        ImageView imgVoltar = findViewById(R.id.imgVoltar);
        imgVoltar.setOnClickListener(v -> {
            Intent rota = new Intent(this, InfoVeiculos.class);
            startActivity(rota);
        });

    }

    public void salvar(Veiculo veiculo, Context c) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("veiculo").document(veiculo.getPlaca()).set(veiculo);
        if (veiculo.getPlaca() != null) {
            db.collection("veiculo").document(String.valueOf(veiculo.getPlaca())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(c, "Veiculo cadastrado com sucesso..!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(c, "Erro ao cadastrar veiculo..!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(c, "Digite algo para realizar o cadastro", Toast.LENGTH_SHORT).show();
        }
    }

    public int verificarExistencia(String placa) {
        for (Veiculo veiculo : listaVeiculo) {
            String placaFirebase = veiculo.getPlaca();

            if (placa.equals(placaFirebase)) {
                return 2;
            }
        }
        return 1;
    }

}
