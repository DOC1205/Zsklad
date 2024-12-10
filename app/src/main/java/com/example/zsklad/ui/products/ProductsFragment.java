package com.example.zsklad.ui.products;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.zsklad.R;
import com.example.zsklad.databinding.FragmentProductsBinding;
import com.example.zsklad.model.Product;
import com.example.zsklad.ui.SharedViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment implements ProductsAdapter.ProductClickListener {
    private FragmentProductsBinding binding;
    private ProductsAdapter adapter;
    private SharedViewModel sharedViewModel;
    private FirebaseDatabase database;
    private DatabaseReference productsRef;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://skladuchet-17c5a-default-rtdb.europe-west1.firebasedatabase.app");
        productsRef = database.getReference("users")
            .child(auth.getCurrentUser().getUid())
            .child("products");

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        setupRecyclerView();
        setupFab();
        loadProducts();
    }

    private void setupRecyclerView() {
        adapter = new ProductsAdapter(this);
        binding.recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerProducts.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabAddProduct.setOnClickListener(v -> showProductDialog(null));
    }

    private void showProductDialog(Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_product, null);
        TextInputEditText nameEdit = dialogView.findViewById(R.id.edit_product_name);
        TextInputEditText quantityEdit = dialogView.findViewById(R.id.edit_product_quantity);
        TextInputEditText categoryEdit = dialogView.findViewById(R.id.edit_product_category);
        TextInputEditText priceEdit = dialogView.findViewById(R.id.edit_product_price);

        // Create dialog first
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        AlertDialog dialog = builder.setTitle(product == null ? R.string.dialog_add_title : R.string.dialog_edit_title)
            .setView(dialogView)
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, null)
            .create();

        // Pre-fill fields if editing
        if (product != null) {
            nameEdit.setText(product.getName());
            quantityEdit.setText(String.valueOf(product.getQuantity()));
            categoryEdit.setText(product.getCategory());
            priceEdit.setText(String.valueOf(product.getPrice()));
        }

        // Setup keyboard navigation
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (hasFocus) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        };

        nameEdit.setOnFocusChangeListener(focusChangeListener);
        quantityEdit.setOnFocusChangeListener(focusChangeListener);
        categoryEdit.setOnFocusChangeListener(focusChangeListener);
        priceEdit.setOnFocusChangeListener(focusChangeListener);

        TextView.OnEditorActionListener editorActionListener = (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (v == nameEdit) quantityEdit.requestFocus();
                else if (v == quantityEdit) categoryEdit.requestFocus();
                else if (v == categoryEdit) priceEdit.requestFocus();
                return true;
            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateAndSave(dialog, product, nameEdit, quantityEdit, categoryEdit, priceEdit);
                return true;
            }
            return false;
        };

        nameEdit.setOnEditorActionListener(editorActionListener);
        quantityEdit.setOnEditorActionListener(editorActionListener);
        categoryEdit.setOnEditorActionListener(editorActionListener);
        priceEdit.setOnEditorActionListener(editorActionListener);

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> 
                validateAndSave(dialog, product, nameEdit, quantityEdit, categoryEdit, priceEdit));
            
            nameEdit.requestFocus();
        });

        dialog.show();
    }

    private void validateAndSave(AlertDialog dialog, Product product,
                               TextInputEditText nameEdit, TextInputEditText quantityEdit,
                               TextInputEditText categoryEdit, TextInputEditText priceEdit) {
        String name = nameEdit.getText().toString().trim();
        String quantityStr = quantityEdit.getText().toString().trim();
        String category = categoryEdit.getText().toString().trim();
        String priceStr = priceEdit.getText().toString().trim();

        if (name.isEmpty()) {
            nameEdit.setError(getString(R.string.error_field_required));
            nameEdit.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            quantityEdit.setError(getString(R.string.error_invalid_quantity));
            quantityEdit.requestFocus();
            return;
        }

        if (category.isEmpty()) {
            categoryEdit.setError(getString(R.string.error_field_required));
            categoryEdit.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            priceEdit.setError(getString(R.string.error_invalid_price));
            priceEdit.requestFocus();
            return;
        }

        // Hide keyboard before dismissing dialog
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = dialog.getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }

        // All inputs are valid, proceed with save
        if (product == null) {
            Product newProduct = new Product(name, quantity, category, price);
            saveProduct(newProduct);
        } else {
            product.setName(name);
            product.setQuantity(quantity);
            product.setCategory(category);
            product.setPrice(price);
            saveProduct(product);
        }

        dialog.dismiss();
    }

    @Override
    public void onEditClick(Product product) {
        showProductDialog(product);
    }

    @Override
    public void onDeleteClick(Product product) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.dialog_yes, (dialog, which) -> {
                deleteProduct(product);
            })
            .setNegativeButton(R.string.dialog_no, null)
            .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public ProductsAdapter getAdapter() {
        return adapter;
    }

    private void updateProducts(List<Product> products) {
        adapter.setProducts(products);
        sharedViewModel.setProducts(products);
    }

    private void loadProducts() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    ArrayList<Product> products = new ArrayList<>();
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            product.setId(productSnapshot.getKey());
                            products.add(product);
                        }
                    }
                    updateProducts(products);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), 
                        getString(R.string.error_loading_data) + ": " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), 
                    getString(R.string.error_loading_data) + ": " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProduct(Product product) {
        if (product.getId() == null) {
            // New product
            String key = productsRef.push().getKey();
            product.setId(key);
            productsRef.child(key).setValue(product);
        } else {
            // Update existing product
            productsRef.child(product.getId()).setValue(product);
        }
    }

    private void deleteProduct(Product product) {
        if (product.getId() != null) {
            productsRef.child(product.getId()).removeValue();
        }
    }
} 