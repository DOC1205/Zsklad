package com.example.zsklad.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.zsklad.R;
import com.example.zsklad.databinding.ItemCategoryBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<CategoryItem> categories = new ArrayList<>();

    public void setCategories(Map<String, Integer> categoryMap) {
        categories.clear();
        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            categories.add(new CategoryItem(entry.getKey(), entry.getValue()));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CategoryItem item) {
            binding.categoryName.setText(item.name);
            binding.itemCount.setText(itemView.getContext().getString(
                R.string.items_in_category, item.count));
        }
    }

    static class CategoryItem {
        final String name;
        final int count;

        CategoryItem(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }
} 