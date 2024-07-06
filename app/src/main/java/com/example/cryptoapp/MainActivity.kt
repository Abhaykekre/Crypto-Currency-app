package com.example.cryptoapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cryptoapp.databinding.ActivityMainBinding
import org.w3c.dom.Text
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvAdapter: RvAdapter
    private lateinit var data:ArrayList<Modal>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        data = ArrayList<Modal>()
        apiData
        rvAdapter=RvAdapter(this,data)
        binding.Rv.layoutManager=LinearLayoutManager(this)
        binding.Rv.adapter=rvAdapter
        binding.search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
               val filterData = ArrayList<Modal>()
                for(item in data){
                    if(item.name.lowercase(Locale.getDefault()).contains(p0.toString().lowercase(Locale.getDefault())))
                    {
                        filterData.add(item)
                    }

                }
                if(filterData.isEmpty())
                {
                    Toast.makeText(this@MainActivity,"No data available",Toast.LENGTH_LONG).show()

                }else{
                    rvAdapter.changeData(filterData)
                }
            }

        })


    }

    val apiData:Unit
        get() {
            val url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"

            val queue = Volley.newRequestQueue(this)
            val jsonObjectRequest:JsonObjectRequest=

                object:JsonObjectRequest(Method.GET,url,null, Response.Listener { 
                    response ->
                    binding.progressBar.isVisible=false
                    try {
                        val dataArray = response.getJSONArray("data")
                        for (i in 0 until dataArray.length())
                        {
                            val dataObject = dataArray.getJSONObject(i)
                            val symbol = dataObject.getString("symbol")
                            val name = dataObject.getString("name")
                            val quote = dataObject.getJSONObject("quote")
                            val USD = quote.getJSONObject("USD")
                            val price = String.format("$"+"%.2f" ,USD.getDouble("price") )

                            data.add(Modal(name,symbol,price.toString()))

                        }
                        rvAdapter.notifyDataSetChanged()
                    } catch (e:Exception) {
                        Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                    }
                },Response.ErrorListener{
                    Toast.makeText(this,"Error1",Toast.LENGTH_LONG).show()
                })
                {
                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String,String>();
                        headers["X-CMC_PRO_API_KEY"] = "2062774b-da47-4258-800f-2f9fff0f02cc"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        }
}


