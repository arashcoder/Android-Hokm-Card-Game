package com.hokm.personal.my.hokm

import android.animation.Animator
import android.animation.AnimatorSet
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_game.*
import com.hokm.personal.my.hokm.model.*
import com.hokm.personal.my.hokm.view.CardImageView
import android.animation.ObjectAnimator
import android.media.MediaPlayer


class GameActivity : AppCompatActivity(), HakemAnimation {
    override fun cardsDealt(numCards: Int, hakem: Direction, hands: Array<MutableList<Card>>) {
        dealCards(numCards, hakem)

    }

    fun dealCards(numCards: Int, initialDirection: Direction){
        var hakemDealingAnimations = mutableListOf<Animator>()
        var direction = initialDirection

        for(i in 0 .. 3) {
            var cards = getNextCards(numCards)
            var delta = 0f
            var property = ""

            val displayMetrics = resources.displayMetrics
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels
            val centerX = (displayMetrics.widthPixels / 2).toFloat()
            var centerY = (displayMetrics.heightPixels / 2).toFloat()
            centerY -= 100f
            when (direction) {
                Direction.BOTTOM -> {
                    delta = centerY
                    property = "translationY"
                }
                Direction.RIGHT -> {
                    delta = centerX
                    property = "translationX"
                }
                Direction.TOP -> {
                    delta = -centerY
                    property = "translationY"
                }
                Direction.LEFT -> {
                    delta = -centerX
                    property = "translationX"
                }
            }

            var animator: Animator = ObjectAnimator()

            for(card in cards) {
                animator = ObjectAnimator.ofFloat(card, property, delta)
                hakemDealingAnimations.add(animator)
            }
            var myDir = direction //needed, otherwise direction doesn't change
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    formHand(cards, myDir)
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })

            direction = game.getNextDirection(direction)
        }

        var set = AnimatorSet()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                if(lastCardIndexDealt > 0) {
                    game.dealCards(4)//dealCards(4, initialDirection)
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}

        })
        set.playSequentially(hakemDealingAnimations)
        set.start()
    }

    fun formHand(cards: List<CardImageView>, direction: Direction){
        val fullWidth = resources.displayMetrics.widthPixels
        val fullHeight = resources.displayMetrics.heightPixels


        if(direction == Direction.BOTTOM) {
            val width = fullWidth/cards.size
            for ((i, card) in cards.withIndex()) {
                card.setImageSource()
                card.x = (i * width).toFloat()

            }
        }

        else{
            for((i, card) in cards.withIndex()){
                //card.x
            }
        }
    }

    var lastCardIndexDealt = 52
    private fun getNextCards(numCards: Int): List<CardImageView> {
         var nextFiveCards: MutableList<CardImageView> = allCardsImages.subList(lastCardIndexDealt - numCards, lastCardIndexDealt)
        var sortedCards = sortCards(nextFiveCards)
        lastCardIndexDealt -= numCards
        return sortedCards
    }

    private fun sortCards(cards: List<CardImageView>): List<CardImageView>{
        return cards.sortedWith(compareBy({ it.card.suit }, {it.card.value}))
    }

    var allCardsImages = mutableListOf<CardImageView>()
    //var tempCardsImages = mutableListOf<CardImageView>()
    //var hakemDealingAnimations = mutableListOf<Animator>()
    //var zIndex = 1f
    lateinit var soundPlayer: MediaPlayer
    private var game =  Game(this)
    private var hakemDetermination = HakemDetermination()


    override fun oneCardDealtForDeterminingHakem(card: Card, direction: Direction) {

        val cardImage = hakemDetermination.getTopCardImage()


        val displayMetrics = resources.displayMetrics
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val centerX = (displayMetrics.widthPixels / 2).toFloat()
        var centerY = (displayMetrics.heightPixels / 2).toFloat()
        centerY -= 100f


        var animator: Animator


        var delta = 0f
        var property = ""

        when (direction) {
            Direction.BOTTOM -> {
                delta = centerY
                property = "translationY"
            }
            Direction.RIGHT -> {
                delta = centerX
                property = "translationX"
            }
            Direction.TOP -> {
                delta = -centerY
                property = "translationY"
            }
            Direction.LEFT -> {
                delta = -centerX
                property = "translationX"
            }
        }

        animator = ObjectAnimator.ofFloat(cardImage, property, delta)
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                //soundPlayer.stop()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                cardImage.setImageSource()
                soundPlayer.start()
            }

        })
        animator.duration = 500
        hakemDetermination.hakemDealingAnimations.add(animator)

        hakemDetermination.cardZIndex++
        hakemDetermination.hakemDealingAnimations.add(
                ObjectAnimator.ofFloat(cardImage, "translationZ", hakemDetermination.cardZIndex)
                        .setDuration(10))
    }

    override fun hakemDetermined() {
        var set = AnimatorSet()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                ResetCardsInMiddle()
                game.dealCards(5)

            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        set.playSequentially(hakemDetermination.hakemDealingAnimations)
        set.start()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initMediaPlayer()

        init(true)

        game.determineHakem()

    }

    fun init(isHakemDetermination: Boolean = false){
        for(card: Card in game.deck.deck){
            var img  = CardImageView(card, this)
            root.addView(img)
            allCardsImages.add(img)
            if(isHakemDetermination) {
                hakemDetermination.cardsImages.add(img)
            }
        }
    }

    fun initMediaPlayer(){
        soundPlayer = MediaPlayer.create(this, R.raw.deal)
    }

    fun ResetCardsInMiddle() {
        clearAllCardsImages()
        init(false)
    }

   private fun clearAllCardsImages(){
       for(image in allCardsImages){
           root.removeView(image)
       }
       allCardsImages.clear()
   }



    class HakemDetermination(var cardsImages: MutableList<CardImageView> = mutableListOf(),
                             var hakemDealingAnimations: MutableList<Animator> = mutableListOf(),
                             var cardZIndex: Float = 1f){
        fun getTopCardImage(): CardImageView{
            val topCard = cardsImages.last()
            cardsImages.removeAt(cardsImages.lastIndex)
            return topCard
        }

    }

}
