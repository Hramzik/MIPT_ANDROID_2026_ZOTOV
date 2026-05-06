package com.example.messenger.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
    private lateinit var progress: ProgressBar
    private lateinit var progressTop: ProgressBar
    private val adapter = MessagesPagingAdapter()
    private val viewModel: MessagesViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MessagesViewModel(RetrofitClient.apiService) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = view.findViewById(R.id.recyclerMessages)
        progressTop = view.findViewById(R.id.progressTop)
        progress = view.findViewById(R.id.progressLoading)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        lifecycleScope.launch {
            viewModel.messagesFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        var initialScrolledToBottom = false
        adapter.addLoadStateListener { state ->
            val prependState = state.prepend
            progressTop.visibility = if (prependState is LoadState.Loading) View.VISIBLE else View.GONE
            val refreshState = state.refresh
            progress.visibility = if (refreshState is LoadState.Loading) View.VISIBLE else View.GONE

            if (!initialScrolledToBottom && refreshState is androidx.paging.LoadState.NotLoading) {
                val count = adapter.itemCount
                if (count > 0) {
                    recycler.scrollToPosition(count - 1)
                    initialScrolledToBottom = true
                }
            }
        }
    }
}
