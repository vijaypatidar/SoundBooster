package com.vkpapps.soundbooster.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnFlingListener
import com.vkpapps.soundbooster.R
import com.vkpapps.soundbooster.connection.ClientHelper
import com.vkpapps.soundbooster.interfaces.OnFragmentAttachStatusListener
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener
import com.vkpapps.soundbooster.interfaces.OnUserListRequestListener
import com.vkpapps.soundbooster.interfaces.OnUsersUpdateListener
import com.vkpapps.soundbooster.ui.adapter.ClientAdapter

/**
 * @author VIJAY PATIDAR
 */
class DashboardFragment : Fragment(), OnUsersUpdateListener {
    private var users: List<ClientHelper>? = null
    private var onUserListRequestListener: OnUserListRequestListener? = null
    private var clientAdapter: ClientAdapter? = null
    private var onNavigationVisibilityListener: OnNavigationVisibilityListener? = null
    private var onFragmentAttachStatusListener: OnFragmentAttachStatusListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        //Nothing to display when user is client
        if (users == null) return
        clientAdapter = ClientAdapter(users, view.context)
        val recyclerView: RecyclerView = view.findViewById(R.id.clientList)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.onFlingListener = object : OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                onNavigationVisibilityListener?.onNavVisibilityChange(velocityY < 0)
                return false
            }
        }
        recyclerView.adapter = clientAdapter
        clientAdapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onNavigationVisibilityListener = context as OnNavigationVisibilityListener
        onUserListRequestListener = context as OnUserListRequestListener
        onFragmentAttachStatusListener = context as OnFragmentAttachStatusListener
        onFragmentAttachStatusListener?.onFragmentAttached(this)
        users = onUserListRequestListener?.onRequestUsers()
    }

    override fun onDetach() {
        super.onDetach()
        onFragmentAttachStatusListener?.onFragmentDetached(this)
        onUserListRequestListener = null
        onNavigationVisibilityListener = null
        onFragmentAttachStatusListener = null
    }

    override fun onUserUpdated() {
        clientAdapter?.notifyDataSetChanged()
    }
}