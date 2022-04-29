package com.hiddendimension.pexelphotos.feature_photo_search.presentation

import androidx.lifecycle.*
import com.hiddendimension.pexelphotos.feature_photo_search.data.repository.PexelPhotoRepository
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.PhotoSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoSearchViewModel @Inject constructor(
    private val repository: PexelPhotoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()  {

    val state: LiveData<UiState>

    val accept: (SearchUiAction) -> Unit

    init {
        val queryLiveData =
            MutableLiveData(savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY)


        state = queryLiveData
            .switchMap { queryString ->
                liveData {
                    val uiState = repository.getSearchResultStream(queryString)
                        .map {
                            UiState(
                                query = queryString,
                                searchResult = it
                            )
                        }
                        .asLiveData(Dispatchers.Main)
                    emitSource(uiState)
                }
            }


        accept = { action ->
            when (action) {
                is SearchUiAction.Search -> queryLiveData.postValue(action.query)
                is SearchUiAction.Scroll -> if (action.shouldFetchMore) {
                    val immutableQuery = queryLiveData.value
                    if (immutableQuery != null) {
                        viewModelScope.launch {
                            repository.requestMore(immutableQuery)
                        }
                    }
                }
            }
        }

    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value?.query
        super.onCleared()
    }
}

private val SearchUiAction.Scroll.shouldFetchMore
    get() = visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount


sealed class SearchUiAction {
    data class Search(val query: String) : SearchUiAction()
    data class Scroll(
        val visibleItemCount: Int,
        val lastVisibleItemPosition: Int,
        val totalItemCount: Int
    ) : SearchUiAction()
}

data class UiState(
    val query: String,
    val searchResult: PhotoSearchResult
)

private const val VISIBLE_THRESHOLD = 5
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "Sun"
