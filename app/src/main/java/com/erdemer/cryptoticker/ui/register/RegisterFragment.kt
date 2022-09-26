package com.erdemer.cryptoticker.ui.register

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.erdemer.cryptoticker.R
import com.erdemer.cryptoticker.databinding.FragmentRegisterBinding
import com.erdemer.cryptoticker.ui.BaseFragment
import com.erdemer.cryptoticker.util.ext.disable
import com.erdemer.cryptoticker.util.ext.enable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    companion object{
        const val LOG_TAG = "RegisterFragment"
    }

    private lateinit var auth: FirebaseAuth
    override fun onCreateFinished() {
        auth = Firebase.auth
    }

    private fun saveUser() {
        auth.createUserWithEmailAndPassword(binding.textFieldMail.editText?.text?.trim().toString(),
            binding.textFieldPassword.editText?.text?.trim().toString())
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(LOG_TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = binding.textFieldFullName.editText?.text?.trim().toString()
                    }
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileUpdateTask ->
                            if (profileUpdateTask.isSuccessful) {
                                Log.d(LOG_TAG, "User profile name added.")
                                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                            }
                        }
                } else {
                    showDialog(getString(R.string.register_authenticate_error),DialogType.ERROR)
                    Log.e(LOG_TAG, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnRegister.setOnClickListener {
            saveUser()
        }
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if ((p0?.length ?: 0) < 6) {
                    binding.textFieldPassword.error = getString(R.string.password_error_text)
                    binding.btnRegister.disable()
                } else {
                    binding.textFieldPassword.error = null
                    binding.btnRegister.enable()
                }
            }

            override fun afterTextChanged(p0: Editable?) = Unit

        })
    }

}