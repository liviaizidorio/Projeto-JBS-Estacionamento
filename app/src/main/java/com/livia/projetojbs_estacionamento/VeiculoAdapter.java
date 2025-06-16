package com.livia.projetojbs_estacionamento;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

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
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_carros_admin, parent, false);
        return new VeiculoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VeiculoViewHolder holder, int position) {
        Veiculo veiculo = listaVeiculos.get(position);

        holder.txtPlaca.setText("Placa: " + veiculo.getPlaca());
        holder.txtEntrada.setText("Entrada: " + veiculo.getEntradaDia() + " às " + veiculo.getEntradaHora());
        holder.txtSaida.setText("Saída: " + veiculo.getSaidaDia() + " às " + veiculo.getSaidaHora());

        if (veiculo.getSaidaDia() != null && !veiculo.getSaidaDia().isEmpty()) {
            holder.txtPermanencia.setText("Permanência: Finalizada");
        } else {
            holder.txtPermanencia.setText("Permanência: Em aberto");
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
    }

    @Override
    public int getItemCount() {
        return listaVeiculos.size();
    }

    public static class VeiculoViewHolder extends RecyclerView.ViewHolder {
        TextView txtPlaca, txtEntrada, txtSaida, txtPermanencia;
        ImageButton btExcluir;

        public VeiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPlaca = itemView.findViewById(R.id.item_carro_placa);
            txtEntrada = itemView.findViewById(R.id.item_carro_entrada);
            txtSaida = itemView.findViewById(R.id.item_carro_saida);
            txtPermanencia = itemView.findViewById(R.id.item_carro_permanencia);
            btExcluir = itemView.findViewById(R.id.bt_excluir_carro);
        }
    }
}
