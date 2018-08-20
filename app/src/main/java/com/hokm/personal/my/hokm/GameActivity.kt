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
    override fun fiveCardsDealt(hands: MutableList<List<Card>>) {
        var centerY = (resources.displayMetrics.heightPixels / 2).toFloat()
        centerY -= 100f
        var set = AnimatorSet()
        var lastFiveCards = hakemCards.takeLast(numCards)
        var animations = mutableListOf<Animator>()
        for(card in lastFiveCards) {
            var animator = ObjectAnimator.ofFloat(card, "translationY", centerY)
            animations.add(animator)
        }
        set.playSequentially(animations)

        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                formHand(lastFiveCards)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        set.start()

    }

    val deck = Deck()
    var allCardsImages = mutableListOf<CardImageView>()
    var hakemCards = mutableListOf<CardImageView>()
    var animations = mutableListOf<Animator>()
    var zIndex = 1f
    lateinit var soundPlayer: MediaPlayer
    lateinit var game: Game


    override fun oneCardDealtForDeterminingHakem(card: Card, direction: Direction) {

        val card = getTopCard()
        card.setImageSource()

        val displayMetrics = resources.displayMetrics
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val centerX = (displayMetrics.widthPixels / 2).toFloat()
        var centerY = (displayMetrics.heightPixels / 2).toFloat()
        centerY -= 100f


        var animator: Animator = ObjectAnimator()


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

        zIndex++
        animator = ObjectAnimator.ofFloat(card, property, delta)
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                //soundPlayer.stop()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                soundPlayer.start()
            }

        })
        animator.duration = 500
        animations.add(animator)
        animations.add(ObjectAnimator.ofFloat(card, "translationZ", zIndex).setDuration(10))
    }

    override fun hakemDetermined() {
        var set = AnimatorSet()
        set.playSequentially(animations)
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                ResetCardsInMiddle()
                game.dealCards()
                //dealCards(5)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        set.start()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initMediaPlayer()

        val deck = Deck()
        init(deck)

        game = Game(deck, this)
        game.determineHakem()

    }

    fun init(deck: Deck){

        for(card: Card in deck.deck){
            var img  = CardImageView(card, this)

            img.setLayoutParams()
            root.addView(img)
            allCardsImages.add(img)
            hakemCards.add(img)
        }
    }

    fun getTopCard(): CardImageView{
        val topCard = hakemCards.last()
        hakemCards.removeAt(hakemCards.lastIndex)
        return topCard
    }

    fun initMediaPlayer(){
        soundPlayer = MediaPlayer.create(this, R.raw.deal)
    }

    fun ResetCardsInMiddle(){
        hakemCards.clear()
        for(image in allCardsImages){
            root.removeView(image)
        }
        allCardsImages.clear()

        for(i in 0 until deck.deck.size){
            var card = deck.deck[i]
            var img  = CardImageView(card, this)
            img.setLayoutParams()
            root.addView(img)
            allCardsImages.add(img)
            hakemCards.add(img)
        }
    }

    fun dealCards(numCards: Int){

    }

    fun formHand(cards: List<CardImageView>){
        val fullWidth = resources.displayMetrics.widthPixels
        val width = fullWidth/cards.size
        for((i, card) in cards.withIndex()){
            card.setImageSource()
            card.x = (i * width).toFloat()
        }
    }
}
