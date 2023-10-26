package com.example.applockdown

import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.applockdown.adapter.AppListAdapter
import com.example.applockdown.data.AppInfo
import com.example.applockdown.databinding.ActivityMainBinding
import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import java.util.Collections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var installedApplist: MutableList<AppInfo>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!hasUsageStatsPermission()) {
            showAlertDialogForPermission()
        } else {
            installedApplist=getInstalledApplications(this)
            loadInstalledApps(installedApplist)
        }
    }

    private fun loadInstalledApps(installedApplist: MutableList<AppInfo>) {
        val appAdapter = AppListAdapter(installedApplist) { enable ->
            if (enable) {
                binding.btnNext.visibility = View.VISIBLE
            } else {
               binding.btnNext.visibility = View.GONE
            }
        }

       binding.installedAppRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.installedAppRecyclerView.adapter = appAdapter
    }

    private fun getInstalledApplications(context:Context): MutableList<AppInfo> {
        val packageManager = context.packageManager ?: return Collections.emptyList<AppInfo>()
        // Querying the PackageManager to get all the InstalledApplications by passing 0 flags
        val applications = packageManager.getInstalledApplications(0)
        val installedApplications = ArrayList<AppInfo>()
        for(applicationInfo in applications) {
            if(!isSystemApplication(applicationInfo)) {
                val applicationName = packageManager.getApplicationLabel(applicationInfo).toString()
                val applicationIcon = packageManager.getApplicationIcon(applicationInfo)
                installedApplications.add(AppInfo(applicationName,applicationIcon,false))
            }
        }
        return installedApplications
    }

    private fun showAlertDialogForPermission() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setIcon(R.drawable.baseline_apps_24)
            .setMessage("This app requires access to your app usage statistics. Please grant the permission.")
            .setPositiveButton("Allow") { _, _ ->
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
                if (hasUsageStatsPermission()) {
                    installedApplist=getInstalledApplications(this)
                    loadInstalledApps(installedApplist)
                }
            }
            .setNegativeButton("Deny") { _, _ ->
                showRationaleDialog()
            }
            .setCancelable(false)
            .show()
    }

    private fun showRationaleDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("This permission is Mandatory to allow")
            .setMessage("This app relies on the access to usage stats.We require permission to fetch all the installed apps in your device.")
            .setPositiveButton("Ok") { _, _ ->
                showAlertDialogForPermission()
            }
            .setCancelable(false)
        builder.create().show()
    }



    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
    private fun isSystemApplication(applicationInfo: ApplicationInfo) :Boolean {
        if((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
            return true
        return false
    }
}