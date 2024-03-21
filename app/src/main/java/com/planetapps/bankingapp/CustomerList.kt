package com.planetapps.bankingapp

import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomerList : AppCompatActivity(),CustomerItemClicked {

    private lateinit var helper: MyDBHelper
    private lateinit var db : SQLiteDatabase
    private lateinit var rs: Cursor
    private lateinit var list: ArrayList<Customer>
    private lateinit var mapp:HashMap<Int,Customer>
    private var firstperson = -1
    private var secondperson = -1
    private lateinit var adapter:MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_list)

        helper = MyDBHelper(applicationContext)
        db = helper .readableDatabase

        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        adapter = MyAdapter(this)
        findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
        fetchData()
    }


    private fun fetchData(){
        rs = db.rawQuery("SELECT * FROM CUSTOMERS",null)
        list  = ArrayList<Customer>()
        mapp = HashMap<Int,Customer>()
        while(rs.moveToNext()) {

            val id = rs.getString(0).toInt()
            val item = Customer(id, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4).toInt())
            list.add(item)
            mapp.put(id,item)
        }
        adapter.updateList(list)
    }


    override fun onCustomerClicked(customer: Customer)
    {
        val inflater: LayoutInflater = LayoutInflater.from(this)
        val subView: View = inflater.inflate(R.layout.customer_info, null)
        subView.findViewById<TextView>(R.id.firstname).text = customer.getFullName()
        subView.findViewById<TextView>(R.id.email).text = customer.getEmail()
        subView.findViewById<TextView>(R.id.balance).text = customer.getBalance().toString()


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Customer info.")
        builder.setView(subView)
        builder.create()

        //performing positive action
        builder.setPositiveButton("Send to") { dialogInterface, which ->

            Toast.makeText(applicationContext, "Select other customer, then click Receive from", Toast.LENGTH_SHORT).show()
            if (firstperson == -1) {
                firstperson = customer.getId()
            } else {
                firstperson = -1
            }
            updateLayout()
        }

        //performing cancel action
        builder.setNeutralButton("Cancel"){dialogInterface , which ->
        }
//        performing negative action
        builder.setNegativeButton("Receive from"){dialogInterface, which ->

            if(firstperson!= -1 && secondperson == -1)
            {
                secondperson = customer.getId()
                if(firstperson == secondperson)
                {
                    Toast.makeText(applicationContext," Select different customers",Toast.LENGTH_SHORT).show()
                    secondperson = -1
                }
            } else {
                secondperson = -1
            }
            updateLayout()
        }
        builder.show()
    }


    fun updateLayout()
    {

        if(firstperson != -1 && secondperson != -1)
        {
            val inflater: LayoutInflater = LayoutInflater.from(this)
            val subView: View = inflater.inflate(R.layout.transaction_info, null)
            var transactionsafe = false
            var balance1 :Int? = 0
            var balance2: Int? = 0
            var p1name:String? = " "
            var p2name:String? = " "
             balance2 = mapp.get(secondperson)?.getBalance()
             balance1 = mapp.get(firstperson)?.getBalance()
             p1name = mapp.get(firstperson)?.getfirstName()
             p2name = mapp.get(secondperson)?.getfirstName()

            subView.findViewById<TextView>(R.id.fromfield).text = p1name
            subView.findViewById<TextView>(R.id.to).text = p2name
            subView.findViewById<TextView>(R.id.balance1).text = balance1.toString()
            subView.findViewById<TextView>(R.id.balance2).text = balance2.toString()

// Transaction AlertDialog
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val etamt = subView.findViewById<EditText>(R.id.amountentered)
            builder.setTitle("Transaction Details")
            builder.setView(subView)
             val customDialog = builder.create()

            //performing positive action
            builder.setPositiveButton("PAY", DialogInterface.OnClickListener { dialog, which ->
                run {

                    val amt: Int = etamt.text.toString().toInt()
                    if (amt != null) {
                        if (amt > balance1!!) {
                            Toast.makeText(
                                applicationContext,
                                "Please enter \n Valid integer amount",
                                Toast.LENGTH_LONG
                            ).show()
                            customDialog.dismiss()
                            updateLayout()
                        }
                        else
                            transactionsafe = true

                        if (transactionsafe) {
                            transferMoney(firstperson, secondperson, amt)
                            firstperson = -1
                            secondperson = -1
                            transactionsafe = false
                            updateLayout()
                            Toast.makeText(applicationContext, "Transaction Succcessful!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
            //performing negative action
            builder.setNegativeButton("Cancel"){dialogInterface, which ->
                firstperson = -1
                secondperson = -1

            }
            builder.show()
        }

    }

    private fun transferMoney(firstperson: Int, secondperson: Int, amt: Int) {
        var c1:Customer = mapp[firstperson]!!
        var c2:Customer = mapp[secondperson]!!
        c1.setBalance(-amt)
        c2.setBalance(+amt)
        helper.updateBalance(c1)
        helper.updateBalance(c2)

        var tmplist = ArrayList<Customer>()
        for(i in mapp.values)
        {
            tmplist.add(i)

        }
        adapter.updateList(tmplist)
    }
}


