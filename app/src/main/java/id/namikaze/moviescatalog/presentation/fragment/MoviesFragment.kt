package id.namikaze.moviescatalog.presentation.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.namikaze.moviescatalog.R
import id.namikaze.moviescatalog.adapter.MovieAdapter
import id.namikaze.moviescatalog.data.Resource
import id.namikaze.moviescatalog.databinding.FragmentMoviesBinding
import id.namikaze.moviescatalog.domain.model.Movie
import id.namikaze.moviescatalog.presentation.viewmodel.MovieViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private val viewModel: MovieViewModel by viewModels()

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    private val args: MoviesFragmentArgs by navArgs()

    private var isLoadMore = false
    private var isLoading = false
    private var pageNumber = 1

    private var statusSearch = false
    private var textSearch = ""

    private val recyclerViewAdapter by lazy {
        MovieAdapter(::navigateToMovieeDetail)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupRecyclerView()
        setupSearch()

        viewModel.movie.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    it.getLoadingStateIfNotHandled()?.let {}
                }
                is Resource.Success -> {
                    it.getSuccessStateIfNotHandled()?.let { data ->
                        setupResponseMovie(data)
                    }
                }
                is Resource.Error -> {
                    it.getErrorStateIfNotHandled()?.let { data ->
                        Toast.makeText(requireContext(), data, Toast.LENGTH_SHORT).show()
                        binding.pbLoadmoreMovies.visibility = View.GONE
                    }
                }
            }
        })

        viewModel.search.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    it.getLoadingStateIfNotHandled()?.let {}
                }
                is Resource.Success -> {
                    it.getSuccessStateIfNotHandled()?.let { data ->
                        setupResponseMovie(data)
                        binding.pbMovies.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    it.getErrorStateIfNotHandled()?.let { data ->
                        Toast.makeText(requireContext(), data, Toast.LENGTH_SHORT).show()
                        binding.pbMovies.visibility = View.GONE
                    }
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMovieList(args.withGenres.toInt(), pageNumber.toString())
        }
    }

    private fun setupUi() {
        binding.tbMovies.title = args.nameGenres
        binding.tbMovies.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.nsvMovies.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY == (v!!.getChildAt(0).measuredHeight - v.measuredHeight)) {
                if (!isLoading) {
                    isLoading = true
                    pageNumber += 1

                    binding.pbLoadmoreMovies.visibility = View.VISIBLE
                    if (!statusSearch) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.getMovieList(args.withGenres.toInt(), pageNumber.toString())
                        }
                    } else {
                        lifecycleScope.launch {
                            viewModel.getSearchMovieList(textSearch, pageNumber.toString())
                        }
                    }
                }
            }
        })
    }

    private fun setupResponseMovie(data: List<Movie>) {
        binding.pbLoadmoreMovies.visibility = View.GONE
        if (!isLoadMore){
            if (!data.isNullOrEmpty()){
                recyclerViewAdapter.setData(data, false)
                isLoadMore = true
            }else {
                binding.rvMovies.visibility = View.GONE
            }
        }else{
            if (isLoading) {
                isLoading = if (data.isNullOrEmpty()){
                    true
                }else{
                    recyclerViewAdapter.setData(data, true)
                    false
                }
            }
        }
    }

    private fun setupRecyclerView() = with(binding.rvMovies) {
        setHasFixedSize(true)
        layoutManager = GridLayoutManager(context, SPAN_COUNT_MOVIE_ITEM)
        adapter = recyclerViewAdapter
    }

    private fun navigateToMovieeDetail(movieId: String) {
        super.getView()?.findNavController()?.navigate(
            MoviesFragmentDirections.actionMoviesFragmentToDetailMovieFragment(movieId)
        )
    }

    private fun setupSearch() {
        binding.edSearch.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (timer != null) {
                    timer!!.cancel()
                }

                timer = object : CountDownTimer(1500, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        if (s.toString().isEmpty()){
                            if (statusSearch){
                                pageNumber = 1
                                statusSearch = false
                                isLoadMore = false
                                binding.tbMovies.title = args.nameGenres
                                viewLifecycleOwner.lifecycleScope.launch {
                                    viewModel.getMovieList(args.withGenres.toInt(), pageNumber.toString())
                                }
                            }
                        } else {
                            textSearch = s.toString()
                            pageNumber = 1
                            statusSearch = true
                            isLoadMore = false
                            binding.tbMovies.title = requireContext().resources.getText(R.string.text_search_movies_2)
                            binding.pbMovies.visibility = View.VISIBLE
                            lifecycleScope.launch {
                                viewModel.getSearchMovieList(s.toString(), pageNumber.toString())
                            }
                        }
                    }
                }.start()
            }
        })
    }

    companion object {
        const val SPAN_COUNT_MOVIE_ITEM = 3
    }

}