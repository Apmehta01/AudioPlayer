package com.example.demoaudioplayer.view.activity.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.demoaudioplayer.R
import com.example.demoaudioplayer.adapter.MyGroupAdapter
import com.example.demoaudioplayer.adapter.MySongsAdapter
import com.example.demoaudioplayer.model.AudioModel
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileDescriptor

class MainActivity : AppCompatActivity() {
    var audioList: ArrayList<AudioModel> = ArrayList()
    var mContext: Context? = null
    var mProgressDialog: ProgressDialog? = null
    var mAdapter: MySongsAdapter? = null
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this@MainActivity
        val timeline_toolbar: Toolbar = findViewById<View>(R.id.toolbar_main) as Toolbar
        setSupportActionBar(timeline_toolbar)
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission()
        loadData()
    }
    /**
     *Name: BaseActivity checkStoragePermission()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will check the storage permission.
     **/
    private fun checkStoragePermission() {
        if (checkAndRequestPermissions()) {
            audioRecyclerView.visibility = View.VISIBLE
            tv_no_data.visibility = View.GONE
            loadData()
        } else {
            audioRecyclerView.visibility = View.GONE
            tv_no_data.visibility = View.VISIBLE
        }
    }

    /**
     * Name: MainActivity checkAndRequestPermissions()
     * <br>Created By Arpan Mehta on 09-02-2022
     * <br>Modified By Arpan Mehta on 09-02-2022
     * <br>Purpose: This method will check required permission of the application.
     *
     */
    private fun checkAndRequestPermissions(): Boolean {
        val listPermissionsNeeded: MutableList<String> = java.util.ArrayList()
        val storagepermission =
            ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (storagepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    /**
     *Name: MainActivity loadData()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will load ProgressDialog and load recyclerview data.
     **/
    private fun loadData() {
        if(mProgressDialog==null){
            mProgressDialog = ProgressDialog(mContext)
        }
        mProgressDialog!!.setTitle("Songs List")
        mProgressDialog!!.setMessage("Please wait.......")
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.show()
        mAdapter = MySongsAdapter(audioList, this)
        Handler(Looper.getMainLooper()).postDelayed({
            setupRecyclerView()
        }, 500)
    }

    /**
     *Name: BaseActivity setupRecyclerView()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will sets up recyclerview from local storage song data.
     **/
    private fun setupRecyclerView() {
        val adapter = MyGroupAdapter()
        audioList.addAll(getSongListWithModel()!!)
        audioList.sortedWith(compareBy<AudioModel> { it.aAlbum })
        val section = Section()
        for (i in 0..audioList.size - 1) {
            section.add(MySongsAdapter(audioList,this@MainActivity))
        }
        adapter.add(section)
        audioRecyclerView.adapter = adapter
        Log.d("COME","COME:>>>>>>>")
        mProgressDialog!!.dismiss()
    }


    @SuppressLint("Range")
    fun getSongListWithModel(): List<AudioModel>? {
        val songlist: MutableList<AudioModel> = ArrayList()
        val contentResolver = contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device
        } else {
            do {
                try {
                    val fullPath: String =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    Log.e("Full Path : ", fullPath)
                    val albumName =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM))
                    val albumID: Long =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    val artist =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST))
                    var albumImage = getAlbumart(albumID)
                    songlist.add(AudioModel(fullPath, albumName, albumImage, artist))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
        }
        return songlist
    }

    fun getAlbumart(album_id: Long?): Bitmap? {
        var bm: Bitmap? = null
        try {
            val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
            val uri = ContentUris.withAppendedId(sArtworkUri, album_id!!)
            val pfd: ParcelFileDescriptor =
                mContext?.getContentResolver()?.openFileDescriptor(uri, "r")!!
            if (pfd != null) {
                val fd: FileDescriptor = pfd.fileDescriptor
                bm = BitmapFactory.decodeFileDescriptor(fd)
            }
        } catch (e: java.lang.Exception) {
        }
        return bm
    }

    override fun onPause() {
        super.onPause()
        mAdapter!!.stopMedia()
    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopMedia()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val perms: MutableMap<String, Int> = HashMap()
        // Initialize the map with both permissions
        perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                // Fill with actual results from user
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    if (perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    ) {
//                        Logger.d(TAG, "Permission granted");
                        checkStoragePermission()
                    } else {
//                        Logger.d(TAG, "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this@MainActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            showDialogOK(
                                mContext!!.resources.getString(R.string.missing_permission_warning),
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE -> {}
                                    }
                                })
                        } else {
                            explainPermissionDialog(mContext!!.resources.getString(R.string.permission_explanation))
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    /**
     * Name: MainActivity showDialogOK()
     * <br>Created By Arpan Mehta on 09-02-2022
     * <br>Modified By Arpan Mehta on 09-02-2022
     * <br>Purpose: This method will show the Alert Dialog on permission cancel.
     * @param message description of the permission.
     * @param okListener click listener of dialog button.
     */
    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(mContext!!.resources.getString(R.string.str_ok), okListener)
            .setNegativeButton(mContext!!.resources.getString(R.string.str_cancel), okListener)
            .create()
            .show()
    }

    /**
     * Name: MainActivity explainPermissionDialog()
     * <br>Created By Arpan Mehta on 09-02-2022
     * <br>Modified By Arpan Mehta on 09-02-2022
     * <br>Purpose: This method will show dialog for explanation of permission.
     * @param msg description of permission.
     */
    private fun explainPermissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(
            mContext!!
        )
        dialog.setMessage(msg)
            .setPositiveButton(
                mContext!!.resources.getString(R.string.str_yes)
            ) { paramDialogInterface, paramInt -> //  permissionsclass.requestPermission(type,code);
                openSettings()
            }
            .setNegativeButton(
                mContext!!.resources.getString(R.string.str_no)
            ) { paramDialogInterface, paramInt -> }
        dialog.show()
    }

    /**
     * Name: MainActivity openSettings()
     * <br>Created By Arpan Mehta on 09-02-2022
     * <br>Modified By Arpan Mehta on 09-02-2022
     * <br>Purpose: This method will redirect to settings for granting permission.
     */
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }
}