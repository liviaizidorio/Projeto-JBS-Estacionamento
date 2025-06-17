package com.livia.projetojbs_estacionamento;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LoginUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText inputNomeUsuario = findViewById(R.id.input_nome_usuario);
        EditText inputEmailUsuario = findViewById(R.id.input_email_usuario);
        TextView telaCadastroUsuario = findViewById(R.id.tela_cadastro_usuario);
        Button btEnviarLogin = findViewById(R.id.bt_enviar_login);
        ImageView imgVoltar = findViewById(R.id.imgVoltar);


        telaCadastroUsuario.setOnClickListener(v -> {
            Intent rota = new Intent(this, CadastroUsuario.class);
            startActivity(rota);
        });

        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btEnviarLogin.setOnClickListener(v -> {
            db.collection("usuario").get().addOnSuccessListener(value -> {
                List<Usuario> argLista = new ArrayList<>();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Usuario objNota = doc.toObject(Usuario.class);
                    argLista.add(objNota);
                }

                if (verificarExistencia(inputNomeUsuario, inputEmailUsuario, argLista)) {
                    String nomeUsuario = inputNomeUsuario.getText().toString().trim();

                    if (!nomeUsuario.isEmpty()) {
                        Intent rota = new Intent(LoginUsuario.this, InfoVeiculos.class);
                        rota.putExtra("NOME_USUARIO", nomeUsuario);
                        startActivity(rota);
                        finish();
                    } else {
                        Toast.makeText(LoginUsuario.this, "Nome de usuário está vazio!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginUsuario.this, "Nome ou e-mail incorretos.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(error -> {
                Toast.makeText(LoginUsuario.this, "Você está off-line neste momento." + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FIREBASE", "Erro ao acessar Firestore", error);
            });
        });

        imgVoltar.setOnClickListener(v -> {
            Intent rota = new Intent(LoginUsuario.this, Cadastro.class);
            startActivity(rota);
        });
    }

    public boolean verificarExistencia(EditText inputNomeUsuario, EditText inputEmailUsuario, List<Usuario> argLista) {
        String nomeDigitado = inputNomeUsuario.getText().toString().trim();
        String emailDigitado = inputEmailUsuario.getText().toString().trim();

        for (Usuario usuario : argLista) {
            String nomeFirebase = usuario.getNome();
            String emailFirebase = usuario.getEmail();

            if (nomeDigitado.equals(nomeFirebase) && emailDigitado.equals(emailFirebase)) {
                return true;
            }
        }
        return false;
    }
}