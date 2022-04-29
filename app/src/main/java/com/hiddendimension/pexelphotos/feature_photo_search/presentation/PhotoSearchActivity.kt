package com.hiddendimension.pexelphotos.feature_photo_search.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hiddendimension.pexelphotos.databinding.ActivityPhotoSearchBinding
import com.hiddendimension.pexelphotos.di.AppModule
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.PhotoSearchResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pexelPhotoSearchActivityBinding =
            ActivityPhotoSearchBinding.inflate(layoutInflater)
        val pexelPhotoSearchActivityView = pexelPhotoSearchActivityBinding.root
        setContentView(pexelPhotoSearchActivityView)

        val pexelPhotoSearchActivityViewModel =
            ViewModelProvider(this, AppModule.provideViewModelFactory(owner = this))
                .get(PhotoSearchViewModel::class.java)

        // connecting layout binding with view model
        pexelPhotoSearchActivityBinding.bindState(
            uiState = pexelPhotoSearchActivityViewModel.state,
            uiActions = pexelPhotoSearchActivityViewModel.accept
        )


    }

    private fun ActivityPhotoSearchBinding.bindState(
        uiState: LiveData<UiState>,
        uiActions: (SearchUiAction) -> Unit
    ) {
        val repoAdapter = PhotosAdapter()
        list.adapter = repoAdapter

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )

        bindList(
            repoAdapter = repoAdapter,
            uiState = uiState,
            onScrollChanged = uiActions
        )

    }


    //binding search state
    private fun ActivityPhotoSearchBinding.bindSearch(
        uiState: LiveData<UiState>,
        onQueryChanged: (SearchUiAction.Search) -> Unit
    ) {
        searchRepo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchRepo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        uiState
            .map(UiState::query)
            .observe(this@PhotoSearchActivity, searchRepo::setText)
    }

    private fun ActivityPhotoSearchBinding.updateRepoListFromInput(onQueryChanged: (SearchUiAction.Search) -> Unit) {
        searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(SearchUiAction.Search(query = it.toString()))
            }
        }
    }


    // list binding
    private fun ActivityPhotoSearchBinding.bindList(
        repoAdapter: PhotosAdapter,
        uiState: LiveData<UiState>,
        onScrollChanged: (SearchUiAction.Scroll) -> Unit
    ) {
        setupScrollListener(onScrollChanged)

        uiState
            .map(UiState::searchResult)
            .observe(this@PhotoSearchActivity) { result ->
                when (result) {
                    is PhotoSearchResult.Success -> {
                        showEmptyList(result.data.isEmpty())
                        repoAdapter.submitList(result.data)
                        repoAdapter.notifyDataSetChanged()
                    }
                    is PhotoSearchResult.Error -> {
                        Toast.makeText(
                            this@PhotoSearchActivity,
                            "\uD83D\uDE28 Wooops $result.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }


    private fun ActivityPhotoSearchBinding.setupScrollListener(
        onScrollChanged: (SearchUiAction.Scroll) -> Unit
    ) {
        val layoutManager = list.layoutManager as LinearLayoutManager
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                onScrollChanged(
                    SearchUiAction.Scroll(
                        visibleItemCount = visibleItemCount,
                        lastVisibleItemPosition = lastVisibleItem,
                        totalItemCount = totalItemCount
                    )
                )
            }
        })
    }

    private fun ActivityPhotoSearchBinding.showEmptyList(show: Boolean) {
        emptyList.isVisible = show
        list.isVisible = !show
    }

}