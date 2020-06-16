package com.merseyside.dropletapp.presentation.view.fragment.free.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.FragmentFreeAccessBinding
import com.merseyside.dropletapp.presentation.base.BaseVpnFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerFreeAccessComponent
import com.merseyside.dropletapp.presentation.di.module.FreeAccessModule
import com.merseyside.dropletapp.presentation.view.fragment.free.adapter.CityAdapter
import com.merseyside.dropletapp.presentation.view.fragment.free.model.FreeAccessViewModel
import com.merseyside.dropletapp.utils.actionBarHelper.ActionBarAnimateHelper
import com.merseyside.merseyLib.AnimatorList
import com.merseyside.merseyLib.Approach
import com.merseyside.merseyLib.animator.AlphaAnimator
import com.merseyside.merseyLib.presentation.view.OnBackPressedListener
import com.merseyside.merseyLib.utils.time.Millis

class FreeAccessFragment : BaseVpnFragment<FragmentFreeAccessBinding, FreeAccessViewModel>(),
    OnBackPressedListener {

    private lateinit var cityAdapter: CityAdapter
    private var spinnerPosition = 0

    private var animatorList: AnimatorList? = null

    private lateinit var helper: ActionBarAnimateHelper

    override val onBackObserver: Observer<Any>
        get() = Observer<Any> {
            helper.reverse()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cityAdapter = CityAdapter(baseActivity)
    }

    override val changeConnectionObserver = Observer<Boolean> { isConnected ->
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
        return R.layout.fragment_free_access
    }

    override fun getTitle(context: Context): String? {
        return ""
    }

    override fun performInjection(bundle: Bundle?) {
        DaggerFreeAccessComponent.builder()
            .appComponent(getAppComponent())
            .freeAccessModule(getFreeAccessModule(bundle))
            .build().inject(this)
    }

    private fun getFreeAccessModule(bundle: Bundle?): FreeAccessModule {
        return FreeAccessModule(this, bundle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doLayout()

        animateToolbar()
    }

    private fun animateToolbar() {

        helper = ActionBarAnimateHelper(
            baseActivity.getToolbar()!!,
            Millis(200)
        )

        helper.setBackgroundColorResAnimated(R.color.free)
        helper.setViewsBackgroundAnimated(ContextCompat.getColor(baseActivity, R.color.free))

        helper.start()
    }

    private fun doLayout() {
        binding.citySpinner.apply {
            adapter = cityAdapter

            onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (cityAdapter.getModel(position).isLocked) {
                            binding.citySpinner.setSelection(spinnerPosition)
                        } else {
                            spinnerPosition = position
                        }
                    }
                }
        }
    }

    override fun onBackPressed(): Boolean {
        goBack()

        return false
    }

    private fun animateConnectionChange() {
        if (animatorList == null) {
            animatorList = AnimatorList(Approach.SEQUENTIALLY).apply {
                addAnimator(AlphaAnimator(AlphaAnimator.Builder(
                    view = binding.typeContainer,
                    duration = DURATION
                ).apply {
                    values(1f, 0f)
                }))

                addAnimator(AlphaAnimator(AlphaAnimator.Builder(
                    view = binding.timeContainer,
                    duration = DURATION
                ).apply {
                    values(0f, 1f)
                }))
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

        fun newInstance(): FreeAccessFragment {
            return FreeAccessFragment()
        }
    }
}