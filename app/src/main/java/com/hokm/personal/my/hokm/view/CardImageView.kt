package com.hokm.personal.my.hokm.view

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.widget.RelativeLayout
import com.hokm.personal.my.hokm.R
import com.hokm.personal.my.hokm.model.Card
import com.squareup.picasso.Picasso

class CardImageView(card: Card, context: Context?) : AppCompatImageView(context) {
    var card = card

    fun setLayoutParams(){
     var params =
             RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
             RelativeLayout.LayoutParams.WRAP_CONTENT)//this.layoutParams as RelativeLayout.LayoutParams
     params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
     this.layoutParams = params
     this.setImageResource(R.drawable.card_back)
     //this.setImageResource(resources.getIdentifier(card.determineImageName(),
      //       "drawable",
       //      "com.hokm.personal.my.hokm"))
 }



    fun setImageSource(){
        val identifier = resources.getIdentifier(card.determineImageName(),
                "drawable",
                "com.hokm.personal.my.hokm")
        Picasso.get().load(identifier).into(this)
       // this.setImageResource()
    }


}