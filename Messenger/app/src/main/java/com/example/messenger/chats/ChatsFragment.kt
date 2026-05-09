package com.example.messenger.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import android.os.Parcelable
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.LoadState
import kotlinx.coroutines.flow.collectLatest
import com.example.messenger.R
import com.example.messenger.network.RetrofitClient
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {

    private val mainVm: com.example.messenger.MainViewModel by activityViewModels()

    private lateinit var recycler: RecyclerView
    private lateinit var progressAppend: ProgressBar
    private lateinit var progressRefresh: ProgressBar

    private lateinit var adapter: ChatsPagingAdapter
    private lateinit var filterAdapter: ChatsFilterAdapter
    private var searchJob: Job? = null

    init {
        initAdapters()
    }

    private fun initAdapters() {
        val onChatClick: (com.example.messenger.network.Chat) -> Unit = { chat ->
            mainVm.select(chat.id)
            if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, com.example.messenger.messages.MessagesFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        adapter = ChatsPagingAdapter(onChatClick)
        filterAdapter = ChatsFilterAdapter(onChatClick)
    }

    private val viewModel: ChatsViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChatsViewModel(RetrofitClient.apiService) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val orientation = resources.configuration.orientation
        if (orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            mainVm.select(null)
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

        val saved = mainVm.getChatsRvState() as? Parcelable
        saved?.let { recycler.layoutManager?.onRestoreInstanceState(it) }

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

    override fun onDestroyView() {
        super.onDestroyView()

        val s = recycler.layoutManager?.onSaveInstanceState()
        mainVm.saveChatsRvState(s)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: android.view.MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_main, menu)
        val item = menu.findItem(R.id.action_search)
        val sv = item?.actionView as? SearchView
        sv?.queryHint = "Поиск чатов"
        sv?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                val q = newText.orEmpty()
                searchJob = lifecycleScope.launch {
                    delay(250)
                    handleQuery(q)
                }
                return true
            }
        })
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

    private fun handleQuery(q: String) {
        if (q.isBlank()) {
            recycler.adapter = adapter
            return
        }

        val list = mutableListOf<com.example.messenger.network.Chat>()
        for (i in 0 until adapter.itemCount) {
            val it = adapter.peekItem(i)
            if (it != null && it.name?.contains(q, ignoreCase = true) == true) list.add(it)
        }

        filterAdapter.submitList(list)
        recycler.adapter = filterAdapter
    }
}
