package com.livia.projetojbs_estacionamento;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class VeiculoAdapter extends RecyclerView.Adapter<VeiculoAdapter.VeiculoViewHolder> {

    private List<Veiculo> listaVeiculos;
    private Context context;

    public VeiculoAdapter(List<Veiculo> listaVeiculos, Context context) {
        this.listaVeiculos = listaVeiculos;
        this.context = context;
    }

    @NonNull
    @Override
    public VeiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_carros, parent, false);
        return new VeiculoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VeiculoViewHolder holder, int position) {
        Veiculo veiculo = listaVeiculos.get(position);

        holder.txtPlaca.setText("Placa: " + veiculo.getPlaca());
        holder.txtEntrada.setText("Entrada: " + veiculo.getEntradaDia() + " \nàs " + veiculo.getEntradaHora());

        if (veiculo.getSaidaDia() != null && !veiculo.getSaidaDia().isEmpty()) {
            holder.txtSaida.setText("Saída: " + veiculo.getSaidaDia() + " \nàs " + veiculo.getSaidaHora());
            holder.txtPermanencia.setText("Status: Finalizado");
        } else {
            holder.txtPermanencia.setText("Status: Em aberto");
            holder.txtSaida.setText("Saída: ");
        }

        if (Cadastro.isAdmin) {
            holder.btExcluir.setVisibility(View.VISIBLE);
            holder.btRegistrarSaida.setVisibility(View.GONE);
        } else {
            holder.btRegistrarSaida.setVisibility(View.VISIBLE);
            holder.btExcluir.setVisibility(View.GONE);
        }

        holder.btExcluir.setOnClickListener(v -> {
            String placa = veiculo.getPlaca();

            FirebaseFirestore.getInstance()
                    .collection("veiculo")
                    .whereEqualTo("placa", placa)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            queryDocumentSnapshots.getDocuments().get(0).getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        listaVeiculos.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, listaVeiculos.size());
                                        Toast.makeText(context, "Veículo excluído", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(context, "Erro ao excluir", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(context, "Veículo não encontrado no Firestore", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Falha ao acessar o Firestore", Toast.LENGTH_SHORT).show());
        });

        holder.btRegistrarSaida.setOnClickListener(v -> {
            showDialogRegistrarSaida(veiculo.getPlaca());
        });
    }

    @Override
    public int getItemCount() {
        return listaVeiculos.size();
    }

    private void showDialogRegistrarSaida(String placa) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.registrar_saida);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextInputEditText inputPlaca = dialog.findViewById(R.id.inputPlaca);
        inputPlaca.setText(placa);

        dialog.findViewById(R.id.fecharCard).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.botaoRegistrar).setOnClickListener(v1 -> {
            String placaDigitada = inputPlaca.getText().toString().trim();

            if (placaDigitada.isEmpty()) {
                Toast.makeText(context, "Digite a placa", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("veiculo")
                    .whereEqualTo("placa", placaDigitada)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentReference docRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdfData = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

                            String dataAtual = sdfData.format(calendar.getTime());
                            String horaAtual = sdfHora.format(calendar.getTime());

                            docRef.update("saidaDia", dataAtual, "saidaHora", horaAtual)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Saída registrada", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Erro ao registrar saída", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(context, "Placa não encontrada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Erro ao acessar Firestore", Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }

    public static class VeiculoViewHolder extends RecyclerView.ViewHolder {
        TextView txtPlaca, txtEntrada, txtSaida, txtPermanencia;
        ImageButton btExcluir;
        Button btRegistrarSaida;

        public VeiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPlaca = itemView.findViewById(R.id.item_carro_placa);
            txtEntrada = itemView.findViewById(R.id.item_carro_entrada);
            txtSaida = itemView.findViewById(R.id.item_carro_saida);
            txtPermanencia = itemView.findViewById(R.id.item_carro_permanencia);
            btExcluir = itemView.findViewById(R.id.bt_excluir_carro2);
            btRegistrarSaida = itemView.findViewById(R.id.bt_registrar_saida);
        }
    }
}
