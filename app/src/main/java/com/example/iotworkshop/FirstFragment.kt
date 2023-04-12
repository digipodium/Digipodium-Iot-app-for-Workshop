package com.example.iotworkshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.iotworkshop.databinding.FragmentFirstBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IotViewModel by lazy {
        IotViewModel(Firebase.database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refresh()
        viewModel.items.observe(viewLifecycleOwner) {
            val itemTimeArray = it.map { item -> item.timestamp }
            val itemHumidityArray = it.map { item -> item.humidity }
            val itemTempArray = it.map { item -> item.temperature }
            val itemDeviceArray = it.map { item -> item.device }

            if (itemTimeArray.isNotEmpty() && itemHumidityArray.isNotEmpty() && itemTempArray.isNotEmpty() && itemDeviceArray.isNotEmpty()) {
                populateLineChart(itemTimeArray, itemHumidityArray, itemTempArray, itemDeviceArray)
            } else {
                binding.lineChartTemp.clear()
                binding.lineChartHumidity.clear()
                AlertDialog.Builder(requireContext()).setTitle("No data")
                    .setMessage("No data available").setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }.show()
            }

        }
        binding.floatingActionButton.setOnClickListener {
            viewModel.refresh()
        }
    }

    private fun populateLineChart(
        itemTimeArray: List<Double>,
        itemHumidityArray: List<Int>,
        itemTempArray: List<Int>,
        itemDeviceArray: List<String>
    ) {
        val lineChartTemp = binding.lineChartTemp
        val lineChartHumidity = binding.lineChartHumidity
        val entries = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()
        for (i in itemTimeArray.indices) {
            entries.add(Entry(i.toFloat(), itemHumidityArray[i].toFloat()))
            entries2.add(Entry(i.toFloat(), itemTempArray[i].toFloat()))
        }

        val dataSet = LineDataSet(entries2, "Temperature")
        dataSet.setDrawCircles(true)
        dataSet.color = resources.getColor(R.color.red, null)
        dataSet.lineWidth = 4f
        dataSet.setValueTextColors(listOf(resources.getColor(R.color.red, null)))
        val data = LineData(dataSet)

        lineChartTemp.animateX(1000, Easing.EaseInSine)
        lineChartTemp.data = data
        lineChartTemp.setPinchZoom(true)
        lineChartTemp.invalidate()

        val dataSet2 = LineDataSet(entries, "Humidity")
        dataSet2.setDrawCircles(true)
        dataSet2.color = resources.getColor(R.color.blue, null)
        dataSet2.lineWidth = 2f
        dataSet2.setValueTextColors(listOf(resources.getColor(R.color.blue, null)))
        val data2 = LineData(dataSet2)

        lineChartHumidity.animateX(1000, Easing.EaseInSine)
        lineChartHumidity.data = data2
        lineChartHumidity.setPinchZoom(true)
        lineChartHumidity.invalidate()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}