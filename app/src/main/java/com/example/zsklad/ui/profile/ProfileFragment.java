package com.example.zsklad.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.zsklad.R;
import com.example.zsklad.databinding.FragmentProfileBinding;
import com.example.zsklad.model.Company;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference companyRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://skladuchet-17c5a-default-rtdb.europe-west1.firebasedatabase.app");
        companyRef = database.getReference("users")
            .child(auth.getCurrentUser().getUid())
            .child("company");

        loadCompanyData();
        setupListeners();
    }

    private void loadCompanyData() {
        companyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Company company = snapshot.getValue(Company.class);
                if (company != null) {
                    binding.editCompanyName.setText(company.getName());
                    binding.editCompanyBin.setText(company.getBin());
                    binding.editCompanyAddress.setText(company.getAddress());
                    binding.editCompanyPhone.setText(company.getPhone());
                    binding.editCompanyEmail.setText(company.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), 
                    getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        binding.btnSaveProfile.setOnClickListener(v -> saveCompanyData());
        
        binding.btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Navigation.findNavController(v)
                .navigate(R.id.action_navigation_profile_to_loginFragment);
        });
    }

    private void saveCompanyData() {
        String name = binding.editCompanyName.getText().toString().trim();
        String bin = binding.editCompanyBin.getText().toString().trim();
        String address = binding.editCompanyAddress.getText().toString().trim();
        String phone = binding.editCompanyPhone.getText().toString().trim();
        String email = binding.editCompanyEmail.getText().toString().trim();

        Company company = new Company(name, address, phone, email, bin);
        companyRef.setValue(company)
            .addOnSuccessListener(unused -> 
                Toast.makeText(requireContext(), 
                    R.string.profile_saved, Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> 
                Toast.makeText(requireContext(), 
                    getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 