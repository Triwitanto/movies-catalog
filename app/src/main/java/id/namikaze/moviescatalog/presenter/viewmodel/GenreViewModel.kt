package id.namikaze.moviescatalog.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.namikaze.moviescatalog.data.Resource
import id.namikaze.moviescatalog.domain.model.Genre
import id.namikaze.moviescatalog.domain.usecase.MovieUseCase
import kotlinx.coroutines.flow.collect

class GenreViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {

    private var _genre = MutableLiveData<Resource<List<Genre>>>()
    val genre: LiveData<Resource<List<Genre>>> = _genre

    suspend fun getGenreList(apiKey: String) {
        movieUseCase.getGenreList(apiKey).collect{
            _genre.postValue(it)
        }
    }
}