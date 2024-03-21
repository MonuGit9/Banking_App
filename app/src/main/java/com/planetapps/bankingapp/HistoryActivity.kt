package com.planetapps.bankingapp

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        findViewById<RecyclerView>(R.id.recyclerviewTxn).layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        findViewById<RecyclerView>(R.id.recyclerviewTxn).adapter = HistoryAdapter(this,)

    }

    }

