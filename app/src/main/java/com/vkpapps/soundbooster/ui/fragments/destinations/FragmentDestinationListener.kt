package com.vkpapps.soundbooster.ui.fragments.destinations

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vkpapps.soundbooster.R

class FragmentDestinationListener(private val activity: AppCompatActivity) : OnDestinationChangedListener {
    private val actionBar: ActionBar? = activity.supportActionBar
    private val navView: BottomNavigationView = activity.findViewById(R.id.nav_view)
    private var previous = R.id.navigation_home
    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        when (destination.id) {
            R.id.navigation_home -> {
                showNavView(true)
                actionBar?.show()
                actionBar?.setBackgroundDrawable(activity.resources.getDrawable(R.color.colorPrimary, activity.theme))
            }
            R.id.navigation_local -> {
                showNavView(true)
                actionBar?.show()
                actionBar?.setBackgroundDrawable(activity.resources.getDrawable(R.color.colorPrimary, activity.theme))
            }
            R.id.navigation_dashboard -> {
                showNavView(true)
                actionBar?.show()
                actionBar?.setBackgroundDrawable(activity.resources.getDrawable(R.color.colorPrimary, activity.theme))
            }
            R.id.navigation_musicPlayer -> {
                actionBar?.hide()
                hideNavView(false)
            }
            R.id.navigation_profile -> {
                actionBar?.setBackgroundDrawable(activity.resources.getDrawable(R.color.profile_frag_background, activity.theme))
                hideNavView(true)
            }


        }

        previous = destination.id
    }

    private fun hideNavView(anim: Boolean) {
        if (navView.visibility == BottomNavigationView.GONE) return
        if (anim) {
            val animation = AnimationUtils.loadAnimation(activity, R.anim.hide_bottom_nav_bar)
            navView.animation = animation
        } else {
            navView.clearAnimation()
        }
        navView.visibility = BottomNavigationView.GONE
    }

    private fun showNavView(anim: Boolean) {
        if (navView.visibility == BottomNavigationView.VISIBLE) return
        if (anim) {
            val animation = AnimationUtils.loadAnimation(activity, R.anim.show_bottom_nav_bar)
            navView.animation = animation
        } else {
            navView.clearAnimation()
        }
        navView.visibility = BottomNavigationView.VISIBLE
    }
}