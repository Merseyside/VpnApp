package com.merseyside.dropletapp.presentation.view.fragment.easyAccess.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.FragmentEasyAccessBinding
import com.merseyside.dropletapp.domain.model.Region
import com.merseyside.dropletapp.presentation.base.BaseVpnFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerEasyAccessComponent
import com.merseyside.dropletapp.presentation.di.module.EasyAccessModule
import com.merseyside.dropletapp.presentation.view.dialog.subscription.view.SubscriptionDialog
import com.merseyside.dropletapp.presentation.view.fragment.easyAccess.adapter.RegionAdapter
import com.merseyside.dropletapp.presentation.view.fragment.easyAccess.model.EasyAccessViewModel
import com.merseyside.dropletapp.utils.actionBarHelper.ActionBarAnimateHelper
import com.merseyside.animators.AnimatorList
import com.merseyside.animators.Approach
import com.merseyside.animators.animator.AlphaAnimator
import com.merseyside.archy.presentation.view.OnBackPressedListener
import com.merseyside.utils.ext.log
import com.merseyside.utils.openUrl
import com.merseyside.utils.time.Millis

class EasyAccessFragment : BaseVpnFragment<FragmentEasyAccessBinding, EasyAccessViewModel>(),
    OnBackPressedListener {

    private lateinit var regionAdapter: RegionAdapter

    private var animatorList: AnimatorList? = null

    private lateinit var helper: ActionBarAnimateHelper

    override val onBackObserver: Observer<Any>
        get() = Observer {
            helper.reverse()
        }

    private val v2RayRequireObserver = Observer<Any> {
        showAlertDialog(
            titleRes = R.string.v2ray_required,
            messageRes = R.string.v2ray_required_msg,
            positiveButtonTextRes = R.string.download,
            negativeButtonTextRes = R.string.error_dialog_negative,
            onPositiveClick = { openUrl(baseActivity, "https://play.google.com/store/apps/details?id=com.github.shadowsocks.plugin.v2ray") }
        )
    }

    private val regionObserver = Observer<List<Region>> {
        regionAdapter = RegionAdapter(baseActivity, it)

        binding.regionSpinner.apply {
            adapter = regionAdapter
        }
    }

    override val changeConnectionObserver = Observer<Boolean> { isConnected ->
        isConnected.log()
        if (isConnected) {
            animateConnectionChange()
        } else {
            reverseConnectionChange()
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_easy_access
    }

    override fun getTitle(context: Context): String? {
        return ""
    }

    override fun performInjection(bundle: Bundle?) {
        DaggerEasyAccessComponent.builder()
            .appComponent(getAppComponent())
            .easyAccessModule(getFreeAccessModule(bundle))
            .build().inject(this)
    }

    private fun getFreeAccessModule(bundle: Bundle?): EasyAccessModule {
        return EasyAccessModule(this, bundle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doLayout()

        animateToolbar()

        viewModel.getRegionLiveData().observe(viewLifecycleOwner, regionObserver)
        viewModel.v2RayRequireEvent.observe(viewLifecycleOwner, v2RayRequireObserver)
    }

    private fun animateToolbar() {

        helper = ActionBarAnimateHelper(
            baseActivity.getToolbar()!!,
            Millis(200)
        )

        helper.setBackgroundColorResAnimated(R.color.secondary_variant)
        helper.setViewsBackgroundAnimated(ContextCompat.getColor(baseActivity, R.color.free))

        helper.start()
    }

    private fun doLayout() {

        binding.subscription.setOnClickListener {
            val dialog = SubscriptionDialog()
            dialog.setOnPurchaseListener(object: SubscriptionDialog.OnPurchaseListener {
                override fun onPurchased() {
                    viewModel.onPurchased()
                }
            })

            dialog.show(parentFragmentManager, "subscriptions")
        }
    }

    override fun onBackPressed(): Boolean {
        goBack()

        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.getRegionLiveData().removeObserver(regionObserver)
        viewModel.v2RayRequireEvent.removeObserver(v2RayRequireObserver)
    }

    private fun animateConnectionChange() {
        if (animatorList == null) {
            val hideAnimatorList = AnimatorList(Approach.TOGETHER).apply {
                addAnimator(AlphaAnimator(AlphaAnimator.Builder(
                    view = binding.typeContainer,
                    duration = DURATION
                ).apply {
                    values(1f, 0f)
                })
                )


                addAnimator(AlphaAnimator(AlphaAnimator.Builder(
                    view = binding.regionContainer,
                    duration = DURATION
                ).apply {
                    values(1f, 0f)
                })
                )
            }

            val showAnimatorList = AnimatorList(Approach.TOGETHER).apply {
                addAnimator(AlphaAnimator(AlphaAnimator.Builder(
                    view = binding.timeContainer,
                    duration = DURATION
                ).apply {
                    values(0f, 1f)
                })
                )

                addAnimator(AlphaAnimator(AlphaAnimator.Builder(
                    view = binding.connectedTitle,
                    duration = DURATION
                ).apply {
                    values(0f, 1f)
                })
                )
            }

            animatorList = AnimatorList(Approach.SEQUENTIALLY).apply {
                addAnimator(hideAnimatorList)
                addAnimator(showAnimatorList)
            }
        }

        animatorList!!.start()
    }

    private fun reverseConnectionChange() {
        if (animatorList != null) {
            animatorList!!.reverse()
        }
    }

    companion object {
        private val DURATION = Millis(400)

        fun newInstance(): EasyAccessFragment {
            return EasyAccessFragment()
        }
    }
}