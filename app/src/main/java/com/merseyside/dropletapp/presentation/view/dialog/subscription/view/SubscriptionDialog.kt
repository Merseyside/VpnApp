package com.merseyside.dropletapp.presentation.view.dialog.subscription.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.databinding.DialogSubscriptionBinding
import com.merseyside.dropletapp.presentation.base.HasAppComponent
import com.merseyside.dropletapp.presentation.di.component.DaggerSubscriptionViewComponent
import com.merseyside.dropletapp.presentation.di.module.SubscriptionViewModule
import com.merseyside.dropletapp.presentation.view.dialog.subscription.model.SubscriptionViewModel
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.dropletapp.subscriptions.VpnSubscription
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
import com.merseyside.merseyLib.presentation.dialog.BaseVMDialog
import com.merseyside.merseyLib.utils.billing.subscriptionPeriod
import javax.inject.Inject


class SubscriptionDialog : BaseVMDialog<DialogSubscriptionBinding, SubscriptionViewModel>(),
    HasAppComponent {

    interface OnPurchaseListener {
        fun onPurchased()
    }

    @Inject
    lateinit var subscriptionManager: SubscriptionManager

    private val clickObserver = Observer<VpnSubscription?> { subscription ->
        if (subscription != null) {
            subscriptionManager.startSubscription(
                baseActivity,
                subscription.skuDetails ?: throw IllegalArgumentException("SkuDetails can not be null"),
                onPurchase = {
                    purchaseListener?.onPurchased()
                    dismiss()
                },
                onError = {}
            )
        }
    }

    private var purchaseListener: OnPurchaseListener? = null

    fun setOnPurchaseListener(listener: OnPurchaseListener) {
        this.purchaseListener = listener
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_subscription
    }

    override fun getStyle(): Int {
        return R.style.CustomAlertDialog
    }

    override fun performInjection(bundle: Bundle?) {
        DaggerSubscriptionViewComponent.builder()
            .appComponent(getAppComponent())
            .subscriptionViewModule(getSubscriptionViewModule(bundle))
            .build().inject(this)
    }

    private fun getSubscriptionViewModule(bundle: Bundle?): SubscriptionViewModule {
        return SubscriptionViewModule(this, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.clickedSubscriptionEvent.observe(baseActivity, clickObserver)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        setLayoutSize(R.dimen.dialog_width_size)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.clickedSubscriptionEvent.removeObserver(clickObserver)
    }
}