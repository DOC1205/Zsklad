package com.example.zsklad.ui.products;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.zsklad.R;
import com.example.zsklad.databinding.ItemProductBinding;
import com.example.zsklad.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();
    private final ProductClickListener listener;

    public interface ProductClickListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public ProductsAdapter(ProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public List<Product> getProducts() {
        return products;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product) {
            binding.productName.setText(product.getName());
            binding.productCategory.setText(product.getCategory());
            binding.productQuantity.setText(String.format(Locale.getDefault(), 
                "Количество: %d", product.getQuantity()));
            binding.productPrice.setText(String.format(
                itemView.getContext().getString(R.string.currency_format), 
                product.getPrice()));

            binding.btnEdit.setOnClickListener(v -> listener.onEditClick(product));
            binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(product));
        }
    }
} 