package com.example.zsklad.ui.auth;

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
import com.example.zsklad.databinding.FragmentRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        auth = FirebaseAuth.getInstance();
        binding.btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = binding.editEmail.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();
        String confirmPassword = binding.editConfirmPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.editEmail.setError(getString(R.string.error_field_required));
            return;
        }

        if (password.isEmpty()) {
            binding.editPassword.setError(getString(R.string.error_field_required));
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.editConfirmPassword.setError(getString(R.string.error_passwords_not_match));
            return;
        }

        if (password.length() < 6) {
            binding.editPassword.setError(getString(R.string.error_invalid_password));
            return;
        }

        binding.btnRegister.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                binding.btnRegister.setEnabled(true);
                if (task.isSuccessful()) {
                    navigateToHome();
                } else {
                    Toast.makeText(requireContext(), 
                        R.string.error_registration_failed, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void navigateToHome() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_registerFragment_to_homeFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 