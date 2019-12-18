package edu.mum.mumwhere

import android.R
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class SearchResultsActivity : Activity() {

    lateinit var strings1:Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {




        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent){
        super.onNewIntent(intent)
        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Toast.makeText(this,"Item selected is " + query, Toast.LENGTH_LONG).show()

            //use the query to search your data somehow
        }
    }

}