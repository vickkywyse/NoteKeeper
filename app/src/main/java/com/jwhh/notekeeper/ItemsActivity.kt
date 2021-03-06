package com.jwhh.notekeeper

import android.app.FragmentTransaction
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.app_bar_items.*
import kotlinx.android.synthetic.main.content_items.*
import kotlinx.android.synthetic.main.layout_settings_toolbar.*

class ItemsActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        IItems,
        ChangePhotoDialog.OnPhotoReceivedListener
{

    private val TAG = "ItemsActivity"

    var accountFragment: AccountFragment? = null

    var settingsFragment: SettingsFragment? = null

    val noteLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    val noteRecyclerAdapter by lazy {
        NoteRecyclerAdapter(this)
    }

    val courseLayoutManager by lazy {
        GridLayoutManager(this, 2)
    }

    val courseRecyclerAdapter by lazy {
        CourseRecyclerAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val activityIntent = Intent(this, NoteActivity::class.java)
            startActivity(activityIntent)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        displayNotes()
    }

    override fun onResume() {
        super.onResume()
        noteRecyclerAdapter.notifyDataSetChanged()
    }

    fun displayNotes() {
        recyclerItems.layoutManager = noteLayoutManager
        recyclerItems.adapter = noteRecyclerAdapter
        nav_view.menu.findItem(R.id.nav_notes).isChecked = true
    }

    fun displayCourses() {
        recyclerItems.layoutManager = courseLayoutManager
        recyclerItems.adapter = courseRecyclerAdapter
        nav_view.menu.findItem(R.id.nav_courses).isChecked = true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }

        correctSettingsToolbarVisibilty()
    }

    fun correctSettingsToolbarVisibilty(){
        if(settingsFragment != null){
            if(settingsFragment!!.isVisible){
                showSettingsAppBar()
            }
            else{
                hideSettingsAppBar()
            }
            return
        }
        hideSettingsAppBar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                inflateSettingsFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override  fun inflateAccountFragment(){
        if(accountFragment == null){
            accountFragment = AccountFragment()
        }
        val transaction: android.support.v4.app.FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.account_container, accountFragment, FRAGMENT_ACCOUNT)
        transaction.addToBackStack(FRAGMENT_ACCOUNT)
        transaction.commit()
    }

    fun inflateSettingsFragment(){
        printToLog("Inflating Settings Fragment")
        if(settingsFragment == null){
            settingsFragment = SettingsFragment()
        }
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.settings_container, settingsFragment, FRAGMENT_SETTINGS)
        transaction.addToBackStack(FRAGMENT_SETTINGS)
        transaction.commit()
    }

    override fun setImageUri(imageUri: Uri?) {
        accountFragment!!.setImageUri(imageUri)
    }

    override fun showSettingsAppBar() {
        settings_app_bar.visibility = View.VISIBLE
    }

    override fun hideSettingsAppBar() {
        settings_app_bar.visibility = View.GONE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_notes -> {
                displayNotes()
            }
            R.id.nav_courses -> {
                displayCourses()
            }
            R.id.nav_share -> {
                showSnackbar("Don't you think you've shared enough")
            }
            R.id.nav_send -> {
                showSnackbar("Send")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(recyclerItems, message, Snackbar.LENGTH_LONG).show()
    }

    private fun printToLog(message: String?){
        Log.d(TAG, message)
    }
}
