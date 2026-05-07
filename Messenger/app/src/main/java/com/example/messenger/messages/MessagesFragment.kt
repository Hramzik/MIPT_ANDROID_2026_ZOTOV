package com.example.messenger.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import kotlinx.coroutines.flow.collectLatest
import com.example.messenger.R
import com.example.messenger.network.RetrofitClient
import kotlinx.coroutines.launch
import android.util.Log

class MessagesFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var progressRefresh: ProgressBar
    private lateinit var progressPrepend: ProgressBar
    private val adapter = MessagesPagingAdapter()
    private val viewModel: MessagesViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val chatId = arguments?.getInt(ARG_CHAT_ID) ?: 1
                @Suppress("UNCHECKED_CAST")
                return MessagesViewModel(RetrofitClient.apiService, chatId) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = view.findViewById(R.id.recyclerMessages)
        progressPrepend = view.findViewById(R.id.progressMessagesPrepend)
        progressRefresh = view.findViewById(R.id.progressMessagesRefresh)
        val editMessage: EditText = view.findViewById(R.id.editMessage)
        val btnSend: Button = view.findViewById(R.id.btnSend)

        recycler.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        recycler.adapter = adapter

        lifecycleScope.launch {
            viewModel.messagesFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        var doNeedToScrollToBottomOnAdapterUpdate = false
        adapter.addLoadStateListener { state ->
            val prependState = state.prepend
            progressPrepend.visibility = if (prependState is LoadState.Loading) View.VISIBLE else View.GONE
            val refreshState = state.refresh
            progressRefresh.visibility = if (refreshState is LoadState.Loading) View.VISIBLE else View.GONE

            if (doNeedToScrollToBottomOnAdapterUpdate && refreshState is androidx.paging.LoadState.NotLoading) {
                val count = adapter.itemCount
                recycler.scrollToPosition(count - 1)
                doNeedToScrollToBottomOnAdapterUpdate = false
            }
        }

        btnSend.setOnClickListener {
            val text = editMessage.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            btnSend.isEnabled = false
            viewModel.sendMessage(text) { ok ->
                lifecycleScope.launch {
                    btnSend.isEnabled = true
                    if (ok) {
                        editMessage.setText("")
                        doNeedToScrollToBottomOnAdapterUpdate = true
                        adapter.refresh()
                    } else {
                        android.util.Log.w("Messenger", "sendMessage failed")
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_CHAT_ID = "chat_id"

        fun newInstance(chatId: Int): MessagesFragment {
            val f = MessagesFragment()
            val args = android.os.Bundle()
            args.putInt(ARG_CHAT_ID, chatId)
            f.arguments = args
            return f
        }
    }
}
