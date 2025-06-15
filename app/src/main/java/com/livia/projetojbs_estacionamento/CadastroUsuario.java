package com.livia.projetojbs_estacionamento;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CadastroUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText inputNomeUsuario = findViewById(R.id.input_nome_usuario);
        EditText inputEmailUsuario = findViewById(R.id.input_email_usuario);
        EditText inputTelefoneUsuario = findViewById(R.id.input_telefone_usuario);
        Button btEnviarLogin = findViewById(R.id.bt_enviar_login);
        ImageView imgVoltar = findViewById(R.id.imgVoltar);

        imgVoltar.setOnClickListener(v -> {
            Intent rota = new Intent(this, LoginUsuario.class);
            startActivity(rota);
        });

        FirebaseApp.initializeApp(this);

        btEnviarLogin.setOnClickListener(v -> {
            String nomeDigitado = inputNomeUsuario.getText().toString().trim();
            String emailDigitado = inputEmailUsuario.getText().toString().trim();
            String telefoneDigitado = inputTelefoneUsuario.getText().toString().trim();

            if (nomeDigitado.isEmpty() || emailDigitado.isEmpty() || telefoneDigitado.isEmpty()) {
                Toast.makeText(CadastroUsuario.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                List<Usuario> argLista = new ArrayList<>();

                db.collection("usuario").get().addOnSuccessListener(value -> {
                    for (DocumentSnapshot document : value) {
                        String nomeFirebase = document.getString("nome");
                        String emailFirebase = document.getString("email");
                        String telefoneFirebase = document.getString("telefone");

                        Usuario usuario = new Usuario(nomeFirebase, emailFirebase, telefoneFirebase);
                        argLista.add(usuario);
                    }

                    int resultado = verificarExistencia(emailDigitado, telefoneDigitado, nomeDigitado, argLista);
                    if (resultado == 1) {
                        Usuario usuario = new Usuario(nomeDigitado, emailDigitado, telefoneDigitado);
                        db.collection("usuario").add(usuario);
                        Toast.makeText(CadastroUsuario.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent rota = new Intent(CadastroUsuario.this, InfoVeiculos.class);
                        startActivity(rota);
                    } else if (resultado == 2) {
                        Toast.makeText(CadastroUsuario.this, "Telefone já cadastrado.", Toast.LENGTH_SHORT).show();
                    } else if (resultado == 3) {
                        Toast.makeText(CadastroUsuario.this, "E-mail já cadastrado.", Toast.LENGTH_SHORT).show();
                    } else if (resultado == 4) {
                        Toast.makeText(CadastroUsuario.this, "E-mail e telefone já cadastrados.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CadastroUsuario.this, "Usuário já cadastrado.", Toast.LENGTH_SHORT).show();
                        Intent rota = new Intent(CadastroUsuario.this, InfoVeiculos.class);
                        startActivity(rota);
                    }
                });
            }
        });

    }
    public int verificarExistencia(String inputEmailUsuario, String inputTelefoneUsuario, String inputNomeUsuario, List<Usuario> argLista) {
        for (Usuario usuario : argLista) {
            String emailFirebase = usuario.getEmail();
            String telefoneFirebase = usuario.getTelefone();
            String nomeFirebase = usuario.getNome();

            if(inputEmailUsuario.equals(emailFirebase) && inputTelefoneUsuario.equals(telefoneFirebase) && inputNomeUsuario.equals(nomeFirebase)){
                return 5;
            }if (inputTelefoneUsuario.equals(telefoneFirebase)) {
                return 2;
            }if (inputEmailUsuario.equals(emailFirebase)) {
                return 3;
            }if(inputTelefoneUsuario.equals(telefoneFirebase) && inputNomeUsuario.equals(nomeFirebase)){
                return 4;
            }
        }
        return 1;
    }
}