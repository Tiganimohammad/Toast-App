package pl.droidsonroids.toast.app.home

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import pl.droidsonroids.toast.R
import pl.droidsonroids.toast.app.Navigator
import pl.droidsonroids.toast.app.base.BaseActivity
import pl.droidsonroids.toast.databinding.ActivityMainBinding
import pl.droidsonroids.toast.utils.Constants.SearchMenuItem.ANIM_DURATION_MILLIS
import pl.droidsonroids.toast.viewmodels.MainViewModel
import javax.inject.Inject


private const val CURRENT_TITLE: String = "current_title"

class MainActivity : BaseActivity() {

    private lateinit var homeFragmentTransaction: HomeFragmentsTransaction

    @Inject
    lateinit var navigator: Navigator

    private val mainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]
    }

    private var navigationDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setupToolbar(savedInstanceState)
        setupNavigationView()
        initHomeFragmentTransaction(showEventsFragment = savedInstanceState == null)

        setupViewModel(mainBinding)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.menuItemAbout -> consume { homeFragmentTransaction.showInfoDialog() }
                else -> super.onOptionsItemSelected(item)
            }

    fun animateSearchButton(offset: Float) {
        searchImageButton
                .animate()
                .y(offset)
                .setDuration(ANIM_DURATION_MILLIS)
                .start()
    }

    private fun setupToolbar(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        savedInstanceState?.let {
            homeTitle.text = it.getString(CURRENT_TITLE)
        }
    }

    private fun setupNavigationView() {
        setHomeNavigationItemReselectedListener()
        setHomeNavigationItemSelectedListener()
    }

    private fun setupViewModel(mainBinding: ActivityMainBinding) {
        mainBinding.mainViewModel = mainViewModel
        navigationDisposable = mainViewModel.navigationSubject
                .subscribe { navigator.dispatch(this, searchImageButton, it) }
    }

    private fun setHomeNavigationItemSelectedListener() {
        homeNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.actionEvents -> {
                    homeFragmentTransaction.showEventsFragment()
                    setHomeTitleText(R.string.events_title)
                }
                R.id.actionSpeakers -> {
                    homeFragmentTransaction.showSpeakersFragment()
                    setHomeTitleText(R.string.speakers_title)
                }
                R.id.actionContact -> {
                    homeFragmentTransaction.showContactFragment()
                    setHomeTitleText(R.string.contact_title)
                }
            }
            true
        }
    }

    private fun setHomeNavigationItemReselectedListener() {
        homeNavigationView.setOnNavigationItemReselectedListener {
            //TODO: Scroll the fragment's content up
        }
    }

    private fun setHomeTitleText(titleRes: Int) {
        homeTitle.text = getText(titleRes)
    }

    private fun initHomeFragmentTransaction(showEventsFragment: Boolean) {
        homeFragmentTransaction = HomeFragmentsTransaction(supportFragmentManager)
        if (showEventsFragment) {
            homeFragmentTransaction.showEventsFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_TITLE, homeTitle.text.toString())
    }

    override fun onDestroy() {
        navigationDisposable?.dispose()
        super.onDestroy()
    }

    private fun consume(func: () -> Unit): Boolean {
        func()
        return true
    }

}

