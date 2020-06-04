package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
import com.merseyside.merseyLib.adapters.BaseAdapter
import com.merseyside.merseyLib.adapters.UpdateRequest
import com.merseyside.merseyLib.utils.Logger
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.VpnStatus

class DropletListFragment : BaseVpnFragment<FragmentDropletListBinding, DropletListViewModel>() {

    private val adapter: DropletAdapter = DropletAdapter()

    private val dropletObserver = Observer<List<Server>> {

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

    override val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as OpenVPNService.LocalBinder
            vpnService = binder.service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            vpnService = null
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
        binding.fab.setOnClickListener { onAddServerClick() }

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

    private fun onAddServerClick() {
        if (VpnStatus.isVPNActive()) {
        showAlertDialog(
            messageRes = R.string.add_server_without_vpn_message,
            positiveButtonTextRes = R.string.add_server_positive,
            negativeButtonTextRes = R.string.add_server_negative,
            onPositiveClick = {
                turnOffVpn()
                viewModel.navigateToAuthScreen()
            })
        } else {
            viewModel.navigateToAuthScreen()
        }
    }

    companion object {

        fun newInstance(): DropletListFragment {
            return DropletListFragment()
        }
    }

    override fun receiveStatus(intent: Intent) {}
    override val changeConnectionObserver = Observer<Boolean> {}
}