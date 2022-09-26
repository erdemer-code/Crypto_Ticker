package com.erdemer.cryptoticker.ui.profile.changeEmail

import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.fragment.findNavController
import com.erdemer.cryptoticker.R
import com.erdemer.cryptoticker.databinding.FragmentChangeEmailBinding
import com.erdemer.cryptoticker.ui.BaseFragment
import com.erdemer.cryptoticker.util.ext.disable
import com.erdemer.cryptoticker.util.ext.enable
import com.erdemer.cryptoticker.util.ext.showToast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ChangeEmailFragment :
    BaseFragment<FragmentChangeEmailBinding>(FragmentChangeEmailBinding::inflate) {
    override fun onCreateFinished() {}

    override fun initListeners() {
        super.initListeners()

        binding.btnChangeEmail.setOnClickListener {
            val user = Firebase.auth.currentUser
            user?.email?.let { email ->
                EmailAuthProvider.getCredential(
                    email,
                    binding.etPasswordOld.text?.trim().toString()
                )
            }?.let {
                user.reauthenticate(it)
            }

            user!!.updatePassword(binding.etPassword.text?.trim().toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        requireContext().showToast("Password updated!")
                        findNavController().popBackStack()
                    } else {
                        showDialog(
                            task.exception?.localizedMessage ?: "Password couldn't change!",
                            DialogType.ERROR
                        )
                    }
                }
        }
        binding.etPasswordOld.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if ((p0?.length ?: 0) < 6) {
                    binding.textFieldOldPassword.error = getString(R.string.password_error_text)
                    binding.btnChangeEmail.disable()
                } else {
                    binding.textFieldOldPassword.error = null
                    binding.btnChangeEmail.enable()
                }
            }

            override fun afterTextChanged(p0: Editable?) = Unit

        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if ((p0?.length ?: 0) < 6) {
                    binding.textFieldPassword.error = getString(R.string.password_error_text)
                    binding.btnChangeEmail.disable()
                } else {
                    binding.textFieldPassword.error = null
                    binding.btnChangeEmail.enable()
                }
            }

            override fun afterTextChanged(p0: Editable?) = Unit

        })
        binding.etRePassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if ((p0?.length ?: 0) < 6) {
                    binding.textFieldRePassword.error = getString(R.string.password_error_text)
                    binding.btnChangeEmail.disable()
                } else if ((p0.toString() != binding.etPassword.text?.trim().toString())) {
                    binding.textFieldRePassword.error =
                        getString(R.string.password_matched_error_text)
                    binding.btnChangeEmail.disable()
                } else {
                    binding.textFieldRePassword.error = null
                    binding.btnChangeEmail.enable()
                }
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })
    }
}