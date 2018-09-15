package com.hokm.personal.my.hokm.view

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.hokm.personal.my.hokm.R
import com.hokm.personal.my.hokm.model.Card
import com.hokm.personal.my.hokm.model.Direction
import com.hokm.personal.my.hokm.util.PrefsHelper
import com.squareup.picasso.Picasso

class CardImageView : AppCompatImageView {

    var card: Card
    var direction: Direction = Direction.TOP

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
     setCardBackIcon()//this.setImageResource(R.drawable.card_back_blue)
 }



    fun setImageSource(){
        val identifier = resources.getIdentifier(card.determineImageName(),
                "drawable",
                "com.hokm.personal.my.hokm")
        Picasso.get().load(identifier).into(this)
    }

    fun setScoreImageSource(){
        Picasso.get().load(R.drawable.card_score).into(this)
    }

    private fun setCardBackIcon(){
        val identifier = resources.getIdentifier(PrefsHelper.getCardBackIcon(),
                "drawable",
                "com.hokm.personal.my.hokm")
        Picasso.get().load(identifier).into(this)
    }
}