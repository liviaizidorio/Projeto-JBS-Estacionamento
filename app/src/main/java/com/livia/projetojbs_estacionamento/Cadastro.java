package com.livia.projetojbs_estacionamento;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Cadastro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button bt_entrar_usuario = findViewById(R.id.bt_entrar_usuario);
        Button bt_add_veiculo = findViewById(R.id.bt_add_veiculo);

        bt_entrar_usuario.setOnClickListener(v -> {
            Intent rota = new Intent(this, LoginUsuario.class);
            startActivity(rota);
        });
        bt_add_veiculo.setOnClickListener(v -> {
            Intent rota = new Intent(this, LoginAdmin.class);
            startActivity(rota);
        });
    }
}