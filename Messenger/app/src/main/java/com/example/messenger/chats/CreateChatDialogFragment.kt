package com.example.messenger.chats

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.example.messenger.network.RetrofitClient
import com.example.messenger.network.RetryExecutor
import kotlinx.coroutines.launch

class CreateChatDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(com.example.messenger.R.layout.dialog_create_chat, null)
        val editName: EditText = view.findViewById(com.example.messenger.R.id.editChatName)
        val progress: ProgressBar = view.findViewById(com.example.messenger.R.id.progressCreatingChat)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Создать чат")
            .setView(view)
            .setNegativeButton("Отмена") { dlg, _ -> dlg.dismiss() }
            .setPositiveButton("Создать", null)

        val dialog = builder.create()
        dialog.setOnShowListener {
            val createChatButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            createChatButton.setOnClickListener {
                val name = editName.text.toString().trim()
                if (name.isEmpty()) {
                    editName.error = "Введите имя"
                    return@setOnClickListener
                }
                createChatButton.isEnabled = false
                progress.visibility = View.VISIBLE

                lifecycleScope.launch {
                    try {
                        val resp = RetryExecutor.executeWithRetry {
                            RetrofitClient.apiService.createChat(name)
                        }
                        if (resp.isSuccessful) {
                            setFragmentResult("create_chat_ok", bundleOf("ok" to true))
                            dialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Создать не удалось: ${resp.code()}", Toast.LENGTH_SHORT).show()
                            createChatButton.isEnabled = true
                            progress.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                        createChatButton.isEnabled = true
                        progress.visibility = View.GONE
                    }
                }
            }
        }

        return dialog
    }
}
