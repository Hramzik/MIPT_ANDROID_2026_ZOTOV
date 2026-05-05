package com.example.messenger.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.network.RetrofitClient
import kotlinx.coroutines.launch
import android.util.Log

class MessagesFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var progress: ProgressBar
    private val adapter = MessagesAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = view.findViewById(R.id.recyclerMessages)
        progress = view.findViewById(R.id.progressLoading)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        loadMessages()
    }

    private fun loadMessages() {
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.apiService.getChat(1)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    adapter.submitList(body?.messages ?: emptyList())
                    Log.d("NetworkTest", "MessagesFragment loaded: ${body?.messages?.size}")
                } else {
                    Log.d("NetworkTest", "MessagesFragment failed: code=${resp.code()}")
                }
            } catch (e: Exception) {
                Log.e("NetworkTest", "MessagesFragment exception", e)
            } finally {
                progress.visibility = View.GONE
            }
        }
    }
}
