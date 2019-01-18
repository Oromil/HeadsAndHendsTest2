package com.oromil.hendsandheadstest.ui.main

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
//import com.google.android.gms.common.api.GoogleApiClient
//import com.google.android.gms.location.FusedLocationProviderClient
import com.oromil.hendsandheadstest.R
import com.oromil.hendsandheadstest.data.entities.StoryEntity
import com.oromil.hendsandheadstest.ui.auth.SignInActivity
import com.oromil.hendsandheadstest.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>() {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> mViewModel.logoutUser()
        }
        return true
    }

    override fun initViews() {
        swipeRefreshLayout = refreshLayout

        swipeRefreshLayout.setOnRefreshListener { mViewModel.update() }

        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsRecyclerView.adapter = NewsAdapter()

        newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy < 0) {
                    btn_top.visibility = View.VISIBLE
                } else btn_top.visibility = View.GONE
            }
        })
        btn_top.setOnClickListener { newsRecyclerView.scrollToPosition(0) }
    }

    override fun subscribeOnViewModelLiveData() {
        mViewModel.update()
        mViewModel.result.observe(this, Observer { data ->
            (newsRecyclerView.adapter as NewsAdapter).updateData(data as List<StoryEntity>)
            swipeRefreshLayout.isRefreshing = false
        })
        mViewModel.logout.observe(this, Observer {
            SignInActivity.start(this)
            finish()
        })

        mViewModel.weather.observe(this, Observer { message ->
            message ?: return@Observer
            showSnackBar(message)
        })


        mViewModel.requestPermissions().observe(this, Observer { listPermissionsNeeded ->
            listPermissionsNeeded ?: return@Observer
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 56)
        })

        mViewModel.requestEnable().observe(this, Observer { status ->
            status ?: return@Observer
            status.startResolutionForResult(this@MainActivity,
                    60)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val permissionLocation = ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            Log.d("", "")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            60 -> when (resultCode) {
                Activity.RESULT_OK -> {
                    mViewModel.getLocation()
                }
                Activity.RESULT_CANCELED -> finish()
            }
        }
    }

    fun showSnackBar(message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("OK") { }
                .show()
    }

    companion object {
        fun start(context: Context) = context
                .startActivity(Intent(context, MainActivity::class.java))
    }
}
