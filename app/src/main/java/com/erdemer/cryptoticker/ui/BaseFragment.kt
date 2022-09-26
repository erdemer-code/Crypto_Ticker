package com.erdemer.cryptoticker.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.erdemer.cryptoticker.R
import com.erdemer.cryptoticker.databinding.GeneralDialogBinding
import com.erdemer.cryptoticker.util.ext.visible


typealias Inflate<T> = (LayoutInflater,ViewGroup?, Boolean) -> T
abstract class BaseFragment<VB: ViewBinding>
protected constructor(
    private val inflate: Inflate<VB>
): Fragment(){

    private var _binding: VB? = null
    val binding get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        onCreateFinished()
    }

    abstract fun onCreateFinished()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    open fun initListeners(){}

    fun showDialog(dialogText: String, dialogType: DialogType, okAction: (() -> Unit)? = null){
        val dialog = Dialog(requireContext())
        val binding = GeneralDialogBinding.inflate(LayoutInflater.from(requireContext()))
        with(dialog){
            setContentView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            with(binding){
                tvDialogTitle.text = dialogText
                when (dialogType){
                    DialogType.SUCCESS -> {
                        ivDialogIcon.setImageResource(R.drawable.check)
                    }
                    DialogType.WARNING -> {
                        ivCloseDialog.visible()
                        ivDialogIcon.setImageResource(R.drawable.warning)
                    }
                    DialogType.ERROR -> {
                        ivDialogIcon.setImageResource(R.drawable.error)
                    }
                }
                btnDialogOk.setOnClickListener {
                    okAction?.invoke()
                    dismiss()
                }
                ivCloseDialog.setOnClickListener {
                    dismiss()
                }
            }
            show()
        }


    }

    enum class DialogType{
        ERROR, SUCCESS, WARNING
    }

}