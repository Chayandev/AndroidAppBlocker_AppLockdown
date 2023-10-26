package com.example.applockdown.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applockdown.R
import com.example.applockdown.data.AppInfo

class AppListAdapter(
    private val appInfoList: List<AppInfo>,
    private val showNextBtn: (Boolean) -> Unit
) :
    RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {
    private var isEnable: Boolean = false
    private val selectedApps = mutableListOf<AppInfo>()

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIconImageView: ImageView = itemView.findViewById(R.id.appIcon)
        val appNameTextView: TextView = itemView.findViewById(R.id.appName)
        val selectView: View = itemView.findViewById(R.id.check_select)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_view, parent, false)
        return AppViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appInfo = appInfoList[position]
        holder.appNameTextView.text = appInfo.appName
        holder.appIconImageView.setImageDrawable(appInfo.appIcon)
        holder.selectView.visibility = View.GONE

        holder.itemView.setOnLongClickListener {
            toggleSelection(appInfo)
            holder.selectView.visibility=View.VISIBLE
            true // Consume the long-click event
        }
        holder.itemView.setOnClickListener {
             if(appInfo.isSelected){
                 appInfo.isSelected=false
                 holder.selectView.visibility=View.GONE
                 selectedApps.remove(appInfo)

                 if(selectedApps.isEmpty()){
                     isEnable=false
                     showNextBtn(false)
                 }
             }else if(isEnable){
                 toggleSelection(appInfo)
                 holder.selectView.visibility=View.VISIBLE
             }

        }
    }

    override fun getItemCount(): Int = appInfoList.size
    private fun toggleSelection(appInfo: AppInfo) {
        isEnable = true
        appInfo.isSelected = true
        selectedApps.add(appInfo)
        showNextBtn(true)
    }

    fun getSelectedApps(): List<AppInfo> {
        return selectedApps.toList()
    }
}