package lv.theashyster.rtu_keep.screens.main

import android.Manifest.permission.*
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE
import lv.theashyster.rtu_keep.*
import lv.theashyster.rtu_keep.screens.element.fragments.BaseFragment.Companion.EXTRA_ACTION
import lv.theashyster.rtu_keep.screens.element.fragments.BaseFragment.Companion.UPDATE
import lv.theashyster.rtu_keep.BuildConfig.APPLICATION_ID
import lv.theashyster.rtu_keep.adapters.NoteItemRecyclerAdapter
import lv.theashyster.rtu_keep.databinding.ActivityMainBinding
import lv.theashyster.rtu_keep.fragments.DeleteDialogFragment
import lv.theashyster.rtu_keep.repositories.NoteItem
import lv.theashyster.rtu_keep.screens.element.ElementActivity
import java.util.*

class MainActivity : AppCompatActivity(), AdapterClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var layoutManager: StaggeredGridLayoutManager

    private lateinit var adapter: NoteItemRecyclerAdapter

    private val viewModel: MainViewModel by viewModels()

    private val items: MutableList<NoteItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = StaggeredGridLayoutManager(GRID_LAYOUT_SPAN_COUNT, VERTICAL).apply {
            gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }
        binding.mainItemsView.layoutManager = layoutManager

        adapter = NoteItemRecyclerAdapter(this, items)
        binding.mainItemsView.adapter = adapter

        binding.mainAddButton.setOnClickListener {
            Intent(this, ElementActivity::class.java).also {
                startActivityForResult(it, REQUEST_NOTE_CREATION)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissionsAndStart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.switchLanguageButton -> {
                switchLanguage()
                true
            }
            R.id.nightModeButton -> {
                if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_NOTE_CREATION && resultCode == RESULT_OK) {
            Toast.makeText(this, getString(R.string.main_note_added_text), LENGTH_SHORT).show()
        }

        if (requestCode == REQUEST_NOTE_MODIFICATION && resultCode == RESULT_OK) {
            data?.let {
                when (it.getIntExtra(EXTRA_ACTION, 0)) {
                    UPDATE -> Toast.makeText(this, getString(R.string.main_note_updated_text), LENGTH_SHORT).show()
                    else -> Toast.makeText(this, getString(R.string.main_note_deleted_text), LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        switchSystemLanguage(newBase)
        super.attachBaseContext(newBase)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isEmpty() ||
            grantResults[CAMERA_PERMISSION_INDEX] == PERMISSION_DENIED ||
            grantResults[READ_EXTERNAL_STORAGE_PERMISSION_INDEX] == PERMISSION_DENIED ||
            grantResults[WRITE_EXTERNAL_STORAGE_PERMISSION_INDEX] == PERMISSION_DENIED
        ) {
            Snackbar.make(binding.mainItemsView, R.string.permissions_denied_explanation, LENGTH_INDEFINITE)
                .setAction(R.string.settings_text) {
                    startActivity(Intent().apply {
                        action = ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", APPLICATION_ID, null)
                        flags = FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            loadItems()
        }
    }

    override fun itemClicked(item: NoteItem) {
        Intent(this, ElementActivity::class.java).also {
            it.putExtra(EXTRA_ITEM_ID, item.id)
            startActivityForResult(it, REQUEST_NOTE_MODIFICATION)
        }
    }

    override fun deleteClicked(item: NoteItem) {
        val dialogFragment = DeleteDialogFragment()
        dialogFragment.action = {
            viewModel.deleteItem(item)
            Toast.makeText(this, getString(R.string.main_note_deleted_text), LENGTH_SHORT).show()
        }
        dialogFragment.show(supportFragmentManager, null)
    }

    private fun permissionsApproved(): Boolean {
        val cameraApproved = PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, CAMERA)
        val readStorageApproved = PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
        val writeStorageApproved =
            PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)

        return cameraApproved && readStorageApproved && writeStorageApproved
    }

    private fun requestPermissions() {
        if (permissionsApproved()) return
        ActivityCompat.requestPermissions(
            this,
            arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSIONS_RESULT_CODE
        )
    }

    private fun checkPermissionsAndStart() {
        if (permissionsApproved()) {
            loadItems()
        } else {
            requestPermissions()
        }
    }

    private fun switchSystemLanguage(context: Context) {
        val configuration = context.resources.configuration
        val systemLocale = if (VERSION.SDK_INT >= VERSION_CODES.N) {
            configuration.locales.get(0)
        } else {
            configuration.locale
        }
        val systemLanguage = systemLocale.language
        val language =
            context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getString(EXTRA_LANGUAGE, systemLanguage)

        language?.let {
            if (systemLanguage != it) {
                val locale = Locale(it)
                Locale.setDefault(locale)
                configuration.setLocale(locale)
                context.createConfigurationContext(configuration)
            }
        }
    }

    private fun switchLanguage() {
        val language = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getString(EXTRA_LANGUAGE, LANGUAGE_ENGLISH)

        if (language == LANGUAGE_ENGLISH) {
            getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putString(EXTRA_LANGUAGE, LANGUAGE_LATVIAN)
                .apply()
        } else {
            getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putString(EXTRA_LANGUAGE, LANGUAGE_ENGLISH)
                .apply()
        }

        recreate()
    }

    private fun loadItems() {
        binding.mainEmptyLayout.visibility = GONE

        viewModel.items.observe(this, {
            it?.let {
                items.clear()
                items.addAll(it)
                adapter.notifyDataSetChanged()

                if (items.size > 0) {
                    binding.mainEmptyLayout.visibility = GONE
                    binding.mainItemsView.visibility = VISIBLE
                } else {
                    binding.mainEmptyLayout.visibility = VISIBLE
                    binding.mainItemsView.visibility = GONE
                }
            }
        })
    }

    companion object {

        private const val REQUEST_NOTE_CREATION = 1
        private const val REQUEST_NOTE_MODIFICATION = 2
        private const val REQUEST_PERMISSIONS_RESULT_CODE = 3
        private const val CAMERA_PERMISSION_INDEX = 0
        private const val READ_EXTERNAL_STORAGE_PERMISSION_INDEX = 1
        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_INDEX = 2
        private const val GRID_LAYOUT_SPAN_COUNT = 2
        private const val LANGUAGE_ENGLISH = "en"
        private const val LANGUAGE_LATVIAN = "lv"
        private const val PREFERENCES_NAME = "lv.theashyster.rtu_keep.preferences.common"
        private const val EXTRA_LANGUAGE = "lv.theashyster.rtu_keep.extra.language"
        const val EXTRA_ITEM_ID = "lv.theashyster.rtu_keep.extra.item_id"
    }
}

interface AdapterClickListener {

    fun itemClicked(item: NoteItem)

    fun deleteClicked(item: NoteItem)
}
