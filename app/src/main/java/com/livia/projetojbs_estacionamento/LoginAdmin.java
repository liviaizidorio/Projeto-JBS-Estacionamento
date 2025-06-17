package com.livia.projetojbs_estacionamento;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginAdmin extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText inputNomeAdmin = findViewById(R.id.input_nome_admin);
        EditText inputEmailAdmin = findViewById(R.id.input_email_admin);
        EditText inputSenhaAdmin = findViewById(R.id.input_telefone_senha);
        TextView telaCadastroAdmin = findViewById(R.id.tela_cadastro_usuario);
        Button btEnviarLogin = findViewById(R.id.bt_enviar_login);
        ImageView imgVoltar = findViewById(R.id.imgVoltar);
        ImageView togglePasswordVisibility = findViewById(R.id.toggle_password_visibility);

        telaCadastroAdmin.setOnClickListener(v -> {
            Intent rota = new Intent(this, CadastroAdmin.class);
            startActivity(rota);
        });

        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Criar admin padrão na coleção "admin"
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("nome", "Administrador");
        adminData.put("email", "admin@exemplo.com");
        adminData.put("senha", "123456");

        db.collection("admin")
                .document("admin_default")
                .set(adminData)
                .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Admin adicionado com sucesso!"))
                .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao adicionar admin: ", e));

        // Login ao clicar no botão
        btEnviarLogin.setOnClickListener(v -> {
            db.collection("admin").get().addOnSuccessListener(value -> {
                List<Admin> argLista = new ArrayList<>();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Admin objAdmin = doc.toObject(Admin.class);
                    argLista.add(objAdmin);
                }
                if (verificarExistencia(inputNomeAdmin, inputEmailAdmin, inputSenhaAdmin, argLista)) {
                    String nomeUsuario = inputNomeAdmin.getText().toString().trim();

                    if (!nomeUsuario.isEmpty()) {
                        Intent rota = new Intent(LoginAdmin.this, ListaCarros.class);
                        rota.putExtra("NOME_USUARIO", nomeUsuario);
                        startActivity(rota);
                        finish();
                    } else {
                        Toast.makeText(LoginAdmin.this, "Nome de usuário está vazio!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginAdmin.this, "Nome, e-mail ou senha incorretos.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(error -> {
                Toast.makeText(LoginAdmin.this, "Você está off-line neste momento. " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FIREBASE", "Erro ao acessar Firestore", error);
            });
        });

        imgVoltar.setOnClickListener(v -> {
            Intent rota = new Intent(LoginAdmin.this, Cadastro.class);
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
    }

    public boolean verificarExistencia(EditText inputNomeAdmin, EditText inputEmailAdmin, EditText inputSenhaAdmin, List<Admin> argLista) {
        String nomeDigitado  = inputNomeAdmin.getText().toString().trim();
        String emailDigitado = inputEmailAdmin.getText().toString().trim();
        String senhaDigitada = inputSenhaAdmin.getText().toString().trim();

        for (Admin admin : argLista) {
            String nomeFirebase = admin.getNome();
            String emailFirebase = admin.getEmail();
            String senhaFirebase = admin.getSenha();

            if (nomeDigitado.equals(nomeFirebase) && emailDigitado.equals(emailFirebase) && senhaDigitada.equals(senhaFirebase)) {
                return true;
            }
        }
        return false;
    }
}
