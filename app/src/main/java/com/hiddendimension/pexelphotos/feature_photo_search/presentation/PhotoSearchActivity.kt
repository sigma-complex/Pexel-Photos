package com.hiddendimension.pexelphotos.feature_photo_search.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.hiddendimension.pexelphotos.databinding.ActivityPhotoSearchBinding
import com.hiddendimension.pexelphotos.di.AppModule
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

    }

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
}