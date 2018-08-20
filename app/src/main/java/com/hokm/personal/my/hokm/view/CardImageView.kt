package com.hokm.personal.my.hokm.view

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.hokm.personal.my.hokm.R
import com.hokm.personal.my.hokm.model.Card
import com.squareup.picasso.Picasso

class CardImageView : AppCompatImageView {

    var card: Card

    constructor(card: Card, context: Context?) : super(context) {
        this.card = card
        setLayoutParams()
    }

    private fun setLayoutParams(){
     var params =
             RelativeLayout.LayoutParams(
                     RelativeLayout.LayoutParams.WRAP_CONTENT,
                     RelativeLayout.LayoutParams.WRAP_CONTENT)
     params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
     this.layoutParams = params
     this.setImageResource(R.drawable.card_back)
 }



    fun setImageSource(){
        val identifier = resources.getIdentifier(card.determineImageName(),
                "drawable",
                "com.hokm.personal.my.hokm")
        Picasso.get().load(identifier).into(this)
    }


}