package com.example.ygolookup

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.json.JSONTokener


class Activity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)
        //get card name that was passed in
        val cardName = intent.getStringExtra("cardName")
        //references to view components
        val textView = findViewById<TextView>(R.id.displayName)
        val imageView = findViewById<ImageView>(R.id.imageView2)

        var url = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name="+cardName+"&misc=yes"
        if (cardName != null) {
            if(cardName.isDigitsOnly())
            {
                url = "https://db.ygoprodeck.com/api/v7/cardinfo.php?id="+cardName+"&misc=yes"
            }
        }

        val queue = Volley.newRequestQueue(this)

// Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->

                val jsonObject = JSONTokener(response).nextValue() as JSONObject
                val jsonArray = jsonObject.getJSONArray("data")

                //reference to card
                val cardObj = jsonArray.getJSONObject(0)

                val imgUrl = cardObj.getJSONArray("card_images").getJSONObject(0).getString("image_url")

                //check if card is banned in TCG format, if so, update info
                var banTCG = "\n\nTCG Restrictions: None"
                if(cardObj.has("banlist_info") && cardObj.getJSONObject("banlist_info").has("ban_tcg"))
                {
                    banTCG = "\n\nTCG Restrictions: " + cardObj.getJSONObject("banlist_info").getString("ban_tcg")
                }


                //check if card has an effect or normal description
                var desc = "\n\nDescription: " + cardObj.getString("desc")
                if(cardObj.getJSONArray("misc_info").getJSONObject(0).getInt("has_effect")==1)
                {
                    desc = "\n\nEffect: " + cardObj.getString("desc")
                }

                //reference to array with card prices
                val priceArr= cardObj.getJSONArray("card_prices").getJSONObject(0)

                //find card specific info and adjust accordingly
                var cardSpecial = ""
                if(cardObj.has("atk"))
                {
                    cardSpecial += "\n\nATK: " + cardObj.getInt("atk")
                }
                if(cardObj.has("def"))
                {
                    cardSpecial += "\n\nDEF: " + cardObj.getInt("def")
                }
                //check to see if card has level/rank, then check if card is an XYZ Monster to decide if it is a rank(API does not distinguish rank/level)
                if(cardObj.has("level"))
                {
                    if(cardObj.getString("type")=="XYZ Monster")
                    {
                        cardSpecial += "\n\nRank: " + cardObj.getString("level")
                    }
                    else
                    {
                        cardSpecial += "\n\nLevel: " + cardObj.getString("level")
                    }
                }
                if(cardObj.has("race"))
                {
                    cardSpecial += "\n\nMonster Type: " + cardObj.getString("race")
                }
                if(cardObj.has("attribute"))
                {
                    cardSpecial += "\n\nMonster Attribute: " + cardObj.getString("attribute")
                }
                if(cardObj.has("scale"))
                {
                    cardSpecial += "\n\nPendulum Scale Value: " + cardObj.getString("scale")
                }
                if(cardObj.has("linkval"))
                {
                    cardSpecial += "\n\nLink Value: " + cardObj.getString("linkval")
                    val linkMarks = cardObj.getJSONArray("linkmarkers")
                    cardSpecial += "\n\nLink Markers: " + linkMarks[0]
                    for (i in 1 until linkMarks.length()) {
                        val item = linkMarks[i]
                        cardSpecial += ", " + item

                    }
                }

                //Display info into
                textView.text = "Name: "+ cardObj.getString("name") + "\n\nID: " + cardObj.getString("id") + "\n\nCard Type: " + cardObj.getString("type") + cardSpecial + banTCG + desc + "\n\nPrices(lowest per vendor)" + "\nTCGPlayer: $" + priceArr.getString("tcgplayer_price") + "\nCardMarket: $" + priceArr.getString("cardmarket_price") + "\nEbay: $" + priceArr.getString("ebay_price") + "\nCoolStuffInc: $" + priceArr.getString("coolstuffinc_price")
                Picasso.get().load(imgUrl).into(imageView)
            },
            { textView.text = "That didn't work!" })



        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

}
