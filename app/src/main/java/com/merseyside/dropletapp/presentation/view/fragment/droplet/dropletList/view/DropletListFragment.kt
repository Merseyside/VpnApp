package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.FragmentDropletListBinding
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.base.BaseVpnFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerDropletListComponent
import com.merseyside.dropletapp.presentation.di.module.DropletListModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.adapter.DropletAdapter
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletListViewModel
import com.merseyside.adapters.base.BaseAdapter
import com.merseyside.adapters.base.UpdateRequest
import com.merseyside.archy.presentation.view.OnBackPressedListener
import com.merseyside.utils.Logger

class DropletListFragment : BaseVpnFragment<FragmentDropletListBinding, DropletListViewModel>(),
    OnBackPressedListener {

    private val adapter: DropletAdapter = DropletAdapter()

    private val dropletObserver = Observer<List<Server>> {

        Logger.log(this, it)

        if (it.isEmpty()) {
            adapter.clear()
        } else {
            if (adapter.isEmpty()) {

                adapter.add(it)
            } else {
                Logger.log(this, it)
                adapter.update(UpdateRequest.Builder(it)
                    .isAddNew(true)
                    .isDeleteOld(true)
                    .build()
                )
            }
        }
    }

    override fun getBindingVariable(): Int {
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

    override fun getLayoutId(): Int {
        return R.layout.fragment_droplet_list
    }

    override fun loadingObserver(isLoading: Boolean) {
    }

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.nav_droplets)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doLayout()

        viewModel.dropletLiveData.observe(viewLifecycleOwner, dropletObserver)
    }

    private fun init() {
        adapter.setOnItemOptionClickListener(object: DropletAdapter.OnItemOptionsClickListener {
            override fun onDelete(server: Server) {
                viewModel.deleteServer(server)
            }

        })
    }

    private fun doLayout() {
        adapter.setOnItemClickListener(onServerClickListener)

        binding.dropletList.adapter = adapter
    }

    private val onServerClickListener = object: BaseAdapter.OnItemClickListener<Server> {
        override fun onItemClicked(obj: Server) {
            viewModel.onServerClick(obj)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        adapter.removeOnItemClickListener(onServerClickListener)

        viewModel.dropletLiveData.removeObserver(dropletObserver)
    }

    override fun onBackPressed(): Boolean {
        goBack()

        return false
    }

    companion object {

        fun newInstance(): DropletListFragment {
            return DropletListFragment()
        }
    }

    override val changeConnectionObserver = Observer<Boolean> {}
}