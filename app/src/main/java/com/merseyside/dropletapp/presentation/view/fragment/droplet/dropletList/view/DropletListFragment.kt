package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.FragmentDropletListBinding
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerDropletListComponent
import com.merseyside.dropletapp.presentation.di.module.DropletListModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.adapter.DropletAdapter
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletItemViewModel
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletListViewModel
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.mvvmcleanarch.presentation.adapter.BaseAdapter
import com.merseyside.mvvmcleanarch.presentation.adapter.UpdateRequest

class DropletListFragment : BaseDropletFragment<FragmentDropletListBinding, DropletListViewModel>() {

    private val adapter: DropletAdapter = DropletAdapter()

    private val dropletObserver = Observer<List<Server>> {

        if (it.isEmpty()) {
            adapter.removeAll()
        } else {
            if (adapter.isEmpty()) {

                adapter.add(it)
            } else {
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

        viewModel.dropletLiveData.observe(this, dropletObserver)
    }

    private fun init() {
        adapter.setOnItemOptionClickListener(object: DropletAdapter.OnItemOptionsClickListener {
            override fun onShareOvpn(server: Server) {
                //viewModel.shareOvpnFile(server)
            }

            override fun onPrepare(server: Server) {
                viewModel.prepareServer(server)
            }

            override fun onConnect(server: Server) {
                connectToVpn(server)
            }

            override fun onDelete(server: Server) {
                viewModel.deleteServer(server)
            }

        })

        adapter.setOnShareClickListener(object: DropletItemViewModel.OnShareClickListener {
            override fun onShareOvpn(server: Server) {
                //viewModel.shareOvpnFile(server)
            }
        })
    }

    private fun connectToVpn(server: Server) {
        if (server.environmentStatus == SshManager.Status.ERROR) {
            showAlertDialog(
                messageRes = R.string.error_dialog_message,
                positiveButtonTextRes = R.string.error_dialog_positive,
                negativeButtonTextRes = R.string.error_dialog_negative,
                onPositiveClick = { viewModel.deleteServer(server) }
            )
        } else {

            viewModel.onServerClick(server)
        }
    }

    private fun doLayout() {
        adapter.setOnItemClickListener(onServerClickListener)

        binding.dropletList.adapter = adapter
    }

    private val onServerClickListener = object: BaseAdapter.OnItemClickListener<Server> {
        override fun onItemClicked(obj: Server) {
            if (obj.environmentStatus == SshManager.Status.PENDING) {
                viewModel.prepareServer(obj)
            } else {
                connectToVpn(obj)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        adapter.removeOnItemClickListener(onServerClickListener)

        viewModel.dropletLiveData.removeObserver(dropletObserver)
    }

    companion object {

        fun newInstance(): DropletListFragment {
            return DropletListFragment()
        }
    }
}