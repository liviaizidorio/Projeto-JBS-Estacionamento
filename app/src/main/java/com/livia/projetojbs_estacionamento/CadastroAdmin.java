package com.livia.projetojbs_estacionamento;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

public class CadastroAdmin extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText inputNomeAdmin = findViewById(R.id.input_nome_admin);
        EditText inputEmailAdmin = findViewById(R.id.input_email_admin);
        EditText inputTelefoneAdmin = findViewById(R.id.input_telefone_admin);
        EditText inputSenhaAdmin = findViewById(R.id.input_telefone_senha);
        Button btEnviarCadastro = findViewById(R.id.bt_enviar_login);
        ImageView imgVoltar = findViewById(R.id.imgVoltar);
        ImageView togglePasswordVisibility = findViewById(R.id.toggle_password_visibility);


        imgVoltar.setOnClickListener(v -> {
            Intent rota = new Intent(this, LoginAdmin.class);
            startActivity(rota);
        });

        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                inputSenhaAdmin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye);
            } else {
                inputSenhaAdmin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_off);
            }
            isPasswordVisible = !isPasswordVisible;
            inputSenhaAdmin.setSelection(inputSenhaAdmin.getText().length());
        });

        FirebaseApp.initializeApp(this);

        btEnviarCadastro.setOnClickListener(v -> {
            String nomeDigitado = inputNomeAdmin.getText().toString().trim();
            String emailDigitado = inputEmailAdmin.getText().toString().trim();
            String telefoneDigitado = inputTelefoneAdmin.getText().toString().trim();
            String senhaDigitada = inputSenhaAdmin.getText().toString().trim();

            if (nomeDigitado.isEmpty() || emailDigitado.isEmpty() || telefoneDigitado.isEmpty() || senhaDigitada.isEmpty()) {
                Toast.makeText(CadastroAdmin.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;  // Para o processamento aqui
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            List<Admin> listaAdmins = new ArrayList<>();

            db.collection("admin").get().addOnSuccessListener(value -> {
                for (DocumentSnapshot document : value) {
                    String nomeFirebase = document.getString("nome");
                    String emailFirebase = document.getString("email");
                    String telefoneFirebase = document.getString("telefone");
                    String senhaFirebase = document.getString("senha");

                    Admin admin = new Admin(nomeFirebase, emailFirebase, telefoneFirebase, senhaFirebase);
                    listaAdmins.add(admin);
                }

                int resultado = verificarExistencia(emailDigitado, telefoneDigitado, nomeDigitado, senhaDigitada, listaAdmins);

                if (resultado == 1) {
                    Admin admin = new Admin(nomeDigitado, emailDigitado, telefoneDigitado, senhaDigitada);
                    db.collection("admin").add(admin);
                    Toast.makeText(CadastroAdmin.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

                    Intent rota = new Intent(CadastroAdmin.this, LoginAdmin.class);
                    rota.putExtra("NOME_USUARIO", nomeDigitado);  // envia o nome junto
                    startActivity(rota);
                    finish();
                } else if (resultado == 2) {
                    Toast.makeText(CadastroAdmin.this, "Telefone já cadastrado.", Toast.LENGTH_SHORT).show();
                } else if (resultado == 3) {
                    Toast.makeText(CadastroAdmin.this, "E-mail já cadastrado.", Toast.LENGTH_SHORT).show();
                } else if (resultado == 4) {
                    Toast.makeText(CadastroAdmin.this, "E-mail e telefone já cadastrados.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CadastroAdmin.this, "Administrador já cadastrado.", Toast.LENGTH_SHORT).show();
                    Intent rota = new Intent(CadastroAdmin.this, InfoVeiculos.class);
                    startActivity(rota);
                    finish();
                }
            });
        });

    }

    public int verificarExistencia(String email, String telefone, String nome, String senha, List<Admin> lista) {
        for (Admin admin : lista) {
            String emailFirebase = admin.getEmail();
            String telefoneFirebase = admin.getTelefone();
            String nomeFirebase = admin.getNome();
            String senhaFirebase = admin.getSenha();

            if (email.equals(emailFirebase) && telefone.equals(telefoneFirebase) && nome.equals(nomeFirebase) && senha.equals(senhaFirebase)) {
                return 5; // Totalmente igual
            }
            if (telefone.equals(telefoneFirebase)) {
                return 2;
            }
            if (email.equals(emailFirebase)) {
                return 3;
            }
            if (telefone.equals(telefoneFirebase) && nome.equals(nomeFirebase)) {
                return 4;
            }
        }
        return 1; // Pode cadastrar
    }
}
