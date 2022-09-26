package com.erdemer.cryptoticker.ui.login

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.erdemer.cryptoticker.R
import com.erdemer.cryptoticker.databinding.FragmentLoginBinding
import com.erdemer.cryptoticker.ui.BaseFragment
import com.erdemer.cryptoticker.util.ext.disable
import com.erdemer.cryptoticker.util.ext.enable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate){
    private lateinit var auth: FirebaseAuth
    override fun onCreateFinished() {
        auth = Firebase.auth
    }

    private fun signIn() {
        auth.signInWithEmailAndPassword(binding.textFieldMail.editText?.text?.trim().toString(),
            binding.textFieldPassword.editText?.text?.trim().toString())
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "signInWithEmail:success")
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    showDialog("Login failed, please check your email or password!",DialogType.ERROR)
                }
            }
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnLogin.setOnClickListener {
            signIn()
        }
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if ((p0?.length ?: 0) < 6) {
                    binding.textFieldPassword.error = getString(R.string.password_error_text)
                    binding.btnLogin.disable()
                } else {
                    binding.textFieldPassword.error = null
                    binding.btnLogin.enable()
                }
            }

            override fun afterTextChanged(p0: Editable?) = Unit

        })
    }
}