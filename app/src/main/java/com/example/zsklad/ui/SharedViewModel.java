package com.example.zsklad.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.zsklad.model.Product;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());

    public void setProducts(List<Product> newProducts) {
        products.setValue(new ArrayList<>(newProducts));
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }
} 