package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletListViewModel
import com.merseyside.dropletapp.databinding.FragmentDropletListBinding
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.di.component.DaggerDropletListComponent
import com.merseyside.dropletapp.presentation.di.module.DropletListModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.adapter.DropletAdapter

class DropletListFragment : BaseDropletFragment<FragmentDropletListBinding, DropletListViewModel>() {

    private lateinit var adapter: DropletAdapter

    private val dropletObserver = Observer<List<Server>> {
        if (!adapter.hasItems()) {
            adapter.add(it)
        } else {
            adapter.update(it)
        }
    }

    override fun setBindingVariable(): Int {
        return BR.viewModel
    }

    override fun performInjection(bundle: Bundle?) {
        DaggerDropletListComponent.builder()
            .appComponent(getAppComponent())
            .dropletListModule(getDropletListModule(bundle))
            .build().inject(this)
    }

    private fun getDropletListModule(bundle: Bundle?): DropletListModule {
        return DropletListModule(this, bundle)
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_droplet_list
    }

    override fun loadingObserver(isLoading: Boolean) {
    }

    override fun getTitle(context: Context): String? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doLayout()
    }

    private fun init() {
        adapter = DropletAdapter()
        adapter.setOnItemOptionClickListener(object: DropletAdapter.OnItemOptionsClickListener {
            override fun onConnect(server: Server) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDelete(server: Server) {
                viewModel.deleteServer(server)
            }

        })

        viewModel.dropletLiveData.observe(this, dropletObserver)
    }

    private fun doLayout() {
        binding.serverList.adapter = adapter

        binding.fab.setOnClickListener {
            viewModel.navigateToAddDropletScreen()
        }

        viewModel.loadServers()

    }

    companion object {
        fun newInstance(): DropletListFragment {
            return DropletListFragment()
        }
    }
}