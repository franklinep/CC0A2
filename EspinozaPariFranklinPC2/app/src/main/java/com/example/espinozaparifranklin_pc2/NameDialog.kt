package com.example.espinozaparifranklin_pc2

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.espinozaparifranklin_pc2.databinding.DialogInputBinding

class NameDialog(
    private val onSubmitClickListener: (String) -> Unit
): DialogFragment() {
    private lateinit var binding : DialogInputBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogInputBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        // Handle the click for the button in the dialog
        binding.bAddNode.setOnClickListener {
            // Get the name input, invoke the listener function, and dismiss the dialog
            val name = binding.teName.text.toString()
            if (name.isNotBlank()) {
                onSubmitClickListener.invoke(name)
                dismiss()
            } else {
                // Optionally, show an error if the input is empty
                binding.teName.error = "Please enter a name"
            }
        }

        // Create and return the dialog
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}
