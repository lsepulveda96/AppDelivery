package com.lsepulveda.kotlinudemydelivery.fragments.delivery

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.adapters.DeliveryTabsPagerAdapter
import com.lsepulveda.kotlinudemydelivery.adapters.RestaurantTabsPagerAdapter

class DeliveryOrdersFragment : Fragment() {
    var miView : View?= null
    var viewPager : ViewPager2?= null
    var tabLayout : TabLayout?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        miView = inflater.inflate(R.layout.fragment_delivery_orders, container, false)

        viewPager = miView?.findViewById(R.id.viewpager)
        tabLayout = miView?.findViewById(R.id.tab_layout)

        tabLayout?.setSelectedTabIndicatorColor(Color.BLACK)
        tabLayout?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        tabLayout?.tabTextColors = ContextCompat.getColorStateList(requireContext(), R.color.black)
       // tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
      //  tabLayout?.isInlineLabel = true

        var numberOfTabs = 3 // numero de tabs

        val adapter = DeliveryTabsPagerAdapter(requireActivity().supportFragmentManager, lifecycle, numberOfTabs)
        viewPager?.adapter = adapter
        viewPager?.isUserInputEnabled = true

        TabLayoutMediator(tabLayout!!, viewPager!!){tab, position ->
            when(position){
                0 -> {
                    tab.text = "DESPACHADO"
                }
                1 -> {
                    tab.text = "EN CAMINO"
                }
                2 -> {
                    tab.text = "ENTREGADO"
                }
            }
        }.attach()

        return miView
    }

}