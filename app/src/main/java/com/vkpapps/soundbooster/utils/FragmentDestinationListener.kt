package com.vkpapps.soundbooster.utils

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import com.vkpapps.soundbooster.R

class FragmentDestinationListener(private val activity: AppCompatActivity) : OnDestinationChangedListener {
    private val actionBar: ActionBar? = activity.supportActionBar
    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        try {
            val id = destination.id
            profile(id)
            musicFragment(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun musicFragment(id: Int) {
        if (id == R.id.navigation_musicPlayer) {
            actionBar!!.hide()
        } else {
            actionBar!!.show()
        }
    }

    private fun profile(id: Int) {
        actionBar!!.elevation = 0f
        if (id == R.id.navigation_profile) {
            actionBar.setBackgroundDrawable(activity.resources.getDrawable(R.color.profile_frag_background, activity.theme))
        } else {
            actionBar.setBackgroundDrawable(activity.resources.getDrawable(R.color.colorPrimary, activity.theme))
        }
    }

}