package com.example.messenger.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import android.os.Parcelable
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import kotlinx.coroutines.flow.collectLatest
import com.example.messenger.R
import kotlinx.coroutines.launch
import android.util.Log

class MessagesFragment : Fragment() {

    private val mainVm: com.example.messenger.MainViewModel by activityViewModels()
    private val viewModel: MessagesViewModel by viewModels()

    private lateinit var recycler: RecyclerView
    private lateinit var progressRefresh: ProgressBar
    private lateinit var progressPrepend: ProgressBar
    private val adapter = MessagesPagingAdapter()

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

        val saved = mainVm.getMessagesRvState() as? Parcelable
        saved?.let { recycler.layoutManager?.onRestoreInstanceState(it) }

        lifecycleScope.launchWhenStarted {
            mainVm.selected.collect { id ->
                viewModel.setChatId(id)
                if (id == null) {
                    adapter.submitData(lifecycle, androidx.paging.PagingData.empty())
                }
                adapter.refresh()
            }
        }

        lifecycleScope.launchWhenStarted {
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

    override fun onDestroyView() {
        super.onDestroyView()

        val s = recycler.layoutManager?.onSaveInstanceState()
        mainVm.saveMessagesRvState(s)
    }
}
