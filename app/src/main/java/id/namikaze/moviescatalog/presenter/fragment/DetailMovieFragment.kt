package id.namikaze.moviescatalog.presenter.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import id.namikaze.moviescatalog.BuildConfig
import id.namikaze.moviescatalog.R
import id.namikaze.moviescatalog.adapter.GenreAdapter
import id.namikaze.moviescatalog.adapter.ReviewAdapter
import id.namikaze.moviescatalog.data.Resource
import id.namikaze.moviescatalog.databinding.FragmentDetailMovieBinding
import id.namikaze.moviescatalog.domain.model.MovieDetail
import id.namikaze.moviescatalog.domain.model.Review
import id.namikaze.moviescatalog.domain.model.Trailer
import id.namikaze.moviescatalog.presenter.viewmodel.DetailMovieViewModel
import id.namikaze.moviescatalog.presenter.viewmodel.GenreViewModel
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class DetailMovieFragment : Fragment() {

    private var _binding: FragmentDetailMovieBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailMovieViewModel by viewModel()

    private val args: DetailMovieFragmentArgs by navArgs()

    private val recyclerViewAdapter by lazy {
        ReviewAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDetailMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.movieDetail.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    it.getLoadingStateIfNotHandled()?.let {

                    }
                }
                is Resource.Success -> {
                    it.getSuccessStateIfNotHandled()?.let {
                        setupUi(it)
                    }
                }
                is Resource.Error -> {
                    it.getErrorStateIfNotHandled()?.let {
                    }
                }
            }
        })

        viewModel.review.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    it.getLoadingStateIfNotHandled()?.let {
                    }
                }
                is Resource.Success -> {
                    it.getSuccessStateIfNotHandled()?.let {
                        binding.pbTeamDetail.visibility = View.GONE
                        setupReview(it)
                    }
                }
                is Resource.Error -> {
                    it.getErrorStateIfNotHandled()?.let {
                    }
                }
            }
        })

        viewModel.trailer.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    it.getLoadingStateIfNotHandled()?.let {

                    }
                }
                is Resource.Success -> {
                    it.getSuccessStateIfNotHandled()?.let {
                        setupTrailer(it)
                    }
                }
                is Resource.Error -> {
                    it.getErrorStateIfNotHandled()?.let {
                    }
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMovieDetail(BuildConfig.API_KEY, args.movieId.toInt())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getReview(BuildConfig.API_KEY, args.movieId.toInt())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getTrailer(BuildConfig.API_KEY, args.movieId.toInt())
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setupUi(data: MovieDetail) = with(data) {
        binding.apply{
            Glide.with(this@DetailMovieFragment).load(BuildConfig.IMAGE_BG_URL + data.backdropPath).placeholder(R.drawable.ic_default_image).into(contentHeader.ivBackgroundMovieDetail)
            Glide.with(this@DetailMovieFragment).load(BuildConfig.IMAGE_URL + data.posterPath).placeholder(R.drawable.ic_default_cover_movie).into(contentHeader.ivCoverMovieDetail)
            contentHeader.tvTitleNameMovieDetail.text = data.title
            contentHeader.tvRatingMovieDetail.text = "${data.voteAverage}/10"
            contentHeader.tvOverviewMovieDetail.text = data.overview
        }
    }

    private fun setupReview(data: List<Review>) = with(data) {
        recyclerViewAdapter.setData(data)
    }

    private fun setupTrailer(data: Trailer) = with(data) {
        if (data.site == "YouTube"){
            binding.ypTrailerMovieDetail.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                    val videoId = data.key.toString()
                    youTubePlayer.loadVideo(videoId, 0f)
                    youTubePlayer.pause()
                }
            })
        } else {
            binding.ypTrailerMovieDetail.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() = with(binding.contentReview.rvReviewMovieDetail) {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        adapter = recyclerViewAdapter
    }
}