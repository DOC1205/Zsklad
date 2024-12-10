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
import com.example.zsklad.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        auth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (auth.getCurrentUser() != null) {
            navigateToHome();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.btnRegister.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment));
    }

    private void loginUser() {
        String email = binding.editEmail.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.editEmail.setError(getString(R.string.error_field_required));
            return;
        }

        if (password.isEmpty()) {
            binding.editPassword.setError(getString(R.string.error_field_required));
            return;
        }

        binding.btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                binding.btnLogin.setEnabled(true);
                if (task.isSuccessful()) {
                    navigateToHome();
                } else {
                    Toast.makeText(requireContext(), 
                        R.string.error_auth_failed, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void navigateToHome() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_loginFragment_to_homeFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 