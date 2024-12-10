package com.example.zsklad.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.zsklad.R;
import com.example.zsklad.databinding.FragmentHomeBinding;
import com.example.zsklad.model.Product;
import com.example.zsklad.ui.SharedViewModel;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private SharedViewModel sharedViewModel;
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        setupCategoryRecyclerView();
        
        sharedViewModel.getProducts().observe(getViewLifecycleOwner(), this::updateStatistics);
    }

    private void setupCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerCategories.setAdapter(categoryAdapter);
    }

    private void updateStatistics(List<Product> products) {
        // Update total products
        binding.totalProducts.setText(String.valueOf(products.size()));
        
        // Update total value
        double totalValue = 0;
        Map<String, Integer> categoryMap = new HashMap<>();
        
        for (Product product : products) {
            totalValue += product.getPrice() * product.getQuantity();
            
            // Update category statistics
            String category = product.getCategory();
            categoryMap.put(category, categoryMap.getOrDefault(category, 0) + 1);
        }
        
        binding.totalValue.setText(String.format(getString(R.string.currency_format), totalValue));
        
        // Update categories
        categoryAdapter.setCategories(categoryMap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 