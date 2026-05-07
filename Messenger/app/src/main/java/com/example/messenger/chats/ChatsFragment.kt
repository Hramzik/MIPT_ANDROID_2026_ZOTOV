package com.example.messenger.chats

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
import kotlinx.coroutines.flow.collectLatest
import com.example.messenger.R
import com.example.messenger.network.RetrofitClient
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private lateinit var recycler: RecyclerView
    private lateinit var progressAppend: ProgressBar
    private lateinit var progressRefresh: ProgressBar
    private val adapter = ChatsPagingAdapter { chat ->
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, com.example.messenger.messages.MessagesFragment.newInstance(chat.id))
            .addToBackStack(null)
            .commit()
    }

    private val viewModel: ChatsViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChatsViewModel(RetrofitClient.apiService) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = view.findViewById(R.id.recyclerChats)
        progressAppend = view.findViewById(R.id.progressChatsAppend)
        progressRefresh = view.findViewById(R.id.progressChatsRefresh)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        lifecycleScope.launch {
            viewModel.chatsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        parentFragmentManager.setFragmentResultListener("create_chat_ok", viewLifecycleOwner) { _, _ ->
            adapter.refresh()
        }

        adapter.addLoadStateListener { state ->
            val appendState = state.append
            progressAppend.visibility = if (appendState is LoadState.Loading) View.VISIBLE else View.GONE
            val refreshState = state.refresh
            progressRefresh.visibility = if (refreshState is LoadState.Loading) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: android.view.MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_create_chat -> {
                CreateChatDialogFragment().show(parentFragmentManager, "create_chat")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
