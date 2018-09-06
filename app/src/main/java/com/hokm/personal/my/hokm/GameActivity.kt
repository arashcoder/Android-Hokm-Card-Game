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
import android.view.ViewGroup
import android.widget.Toast
import android.content.DialogInterface
import android.R.array
import android.support.v7.app.AlertDialog
import android.transition.Visibility
import android.util.Log
import android.view.View
import com.hokm.personal.my.hokm.util.PrefsHelper
import com.squareup.picasso.Picasso


class GameActivity : AppCompatActivity(), HakemAnimation {


    var cardWidth: Int = 0
    var cardHeight: Int = 0
    var hands: Array<MutableList<CardImageView>> = Array(4){ mutableListOf<CardImageView>() }
    var lastCardIndexDealt = 52

    override fun tableComplete(winnerDirection: Direction?, winnerScore: Int, table: List<Card>, isGameOver: Boolean) {
        //directionHakemDetermination is either bottom or right
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels
        val centerX = (displayMetrics.widthPixels / 2).toFloat()
        var centerY = (displayMetrics.heightPixels / 2).toFloat()
        val offsetX = 150f
        val marginX = 100f
        val marginY = 100f

        var animatorSet = AnimatorSet()

        var anSet = AnimatorSet()
        var animList = mutableListOf<Animator>()
        table.forEach {
            var img = findCardImage(it)



            var x = 0f
            var y = 0f
            when(winnerDirection){
                Direction.BOTTOM -> {
                    x = offsetX + (winnerScore-1) * marginX
                    y =  screenHeight - (3*cardHeight.toFloat())
                }
                Direction.RIGHT -> {
                    x = screenWidth - (cardWidth.toFloat() + (cardWidth/2))
                    y =  2 * cardHeight.toFloat() + ((winnerScore-1) * marginY)
                }
            }
            val animator = ObjectAnimator.ofFloat(img, "x", x)
            val animator2 = ObjectAnimator.ofFloat(img, "y", y)

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {

                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {
                    if(img.direction == Direction.RIGHT || img.direction == Direction.LEFT){
                        img.rotation = 0f
                    }
                    img.setScoreImageSource()
                }

            })
            //animator.startDelay = 2000
            //animator2.startDelay = 2000
            animList.add(animator)
            animList.add(animator2)

//            animatorSet.addListener(object : Animator.AnimatorListener {
//                override fun onAnimationRepeat(animation: Animator?) {}
//                override fun onAnimationEnd(animation: Animator?) {
//                  game.playCard()
//                }
//
//                override fun onAnimationCancel(animation: Animator?) {}
//                override fun onAnimationStart(animation: Animator?) {}
//
//            })

            //animator.duration = 500
            //animatorSet.playTogether(animator, animator2)
            //animatorSet.start()
        }

        anSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                if(isGameOver){
                    Toast.makeText(this@GameActivity, "ended", Toast.LENGTH_LONG).show()
                    btn_new_game.visibility = View.VISIBLE
                    btn_new_game.setOnClickListener{
                        hands.forEach { it.clear() }
                        lastCardIndexDealt = 52
                        game.newGame()//game = Game(this@GameActivity)
                        ResetCardsInMiddle(false)
                        game.dealCards(5)
                    }
                }
                else {
                    game.playCard()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}

        })
        anSet.duration = 500
        anSet.startDelay = 1000
        anSet.playTogether(animList)
        anSet.start()
    }

    fun findCardImage(card: Card): CardImageView{
        return allCardsImages.first { it.card == card  }
    }



    override fun cardsDealt(numCards: Int, hakem: Direction, hands: Array<MutableList<Card>>, shouldDetermineHokm: Boolean, hokm: Suit?) {
        dealCards(numCards, hakem, hands, shouldDetermineHokm, hokm)
    }

    fun dealCards(numCards: Int, initialDirection: Direction, nextCards: Array<MutableList<Card>>, shouldDetermineHokm: Boolean, hokm: Suit?=null){

        var hakemDealingAnimations = mutableListOf<Animator>()
        var direction = initialDirection

        for(i in 0 .. 3) {
            var cards = getNextCards(nextCards[direction.value], numCards, direction)

            if(direction == Direction.BOTTOM){
                for(cardImage in cards) {
                    addClickListener(cardImage)
                }
            }

            hands[i].addAll(cards)
            hands[i] = sortCards(hands[i]).toMutableList()

            val delta = getOffset(cards[0], direction)
            val property = getProperty(direction)

            var rotationAnim = ObjectAnimator()
            var animator: Animator = ObjectAnimator()
            animator.duration = 100

            for(card in cards) {
                animator = ObjectAnimator.ofFloat(card, property, delta)

//                rotationAnim = ObjectAnimator.ofFloat(card, "rotation", 90f)
//                rotationAnim.duration = 1
//                hakemDealingAnimations.add(rotationAnim)
                hakemDealingAnimations.add(animator)
            }
            var myDir = direction //needed, otherwise directionHakemDetermination doesn't change
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    formHand(hands[i], myDir)
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {
//                    if(myDir == Direction.RIGHT || myDir == Direction.LEFT){
//                        hands[i].forEach { it.rotation = 90f }
//                    }
                }

            })

            direction = game.getNextDirection(direction)
        }

        var set = AnimatorSet()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                if(hokm != null){
                    setHokmImage(hokm)
                }
                if(shouldDetermineHokm){
                    showHokmDialog()
                }
                else if(lastCardIndexDealt > 0) {
                    game.dealCards(4)//dealCards(4, initialDirection)
                }
                else{
                    game.playCard()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}

        })
        set.playSequentially(hakemDealingAnimations)
        set.start()
    }

    private fun setHokmImage(hokm: Suit) {
        var hokmIcon = when(hokm){
            Suit.CLUB -> "clubs"
            Suit.DIAMOND -> "diamonds"
            Suit.HEART -> "hearts"
            Suit.SPADE -> "spades"
        }

        val identifier = resources.getIdentifier(hokmIcon,
                "drawable",
                "com.hokm.personal.my.hokm")
        Picasso.get().load(identifier).into(img_hokm)
    }

    fun formHand(cards: List<CardImageView>, direction: Direction, doRotation: Boolean = true){
        val fullWidth = resources.displayMetrics.widthPixels
        val fullHeight = resources.displayMetrics.heightPixels



        if(direction == Direction.BOTTOM) {
            val width = fullWidth/cards.size
            for ((i, card) in cards.withIndex()) {
                card.setImageSource()
                card.x = (i * width).toFloat()
                card.translationZ = i.toFloat()
            }
        }

        else{
            for((i, card) in cards.withIndex()){
                card.setImageSource()
                card.translationZ = i.toFloat()

                when(direction){
                    Direction.LEFT -> {
                        if(doRotation){
                            card.rotation = 90f
                        }
                        card.x = (-cardWidth/2).toFloat()
                        card.y = (fullHeight / 3) + (i*cardWidth/6).toFloat()
                    }
                    Direction.RIGHT -> {
                        if(doRotation){
                            card.rotation = 90f
                        }

                        card.x = fullWidth - (cardWidth/2).toFloat()
                        card.y = 150+(i*(cardWidth/2)).toFloat()//(fullHeight / 3) + (i*(cardWidth/6)).toFloat()
                    }
                    Direction.TOP -> {
                        val width = fullWidth/cards.size
                        card.x = (i * width).toFloat()//(fullWidth/3) + (i*(cardWidth/6)).toFloat()
                        card.y = -(cardHeight/2).toFloat()
                    }
                }

            }
        }
    }


    private fun getNextCards(cards: List<Card>, numCards: Int, direction: Direction): List<CardImageView> {
         var nextFiveCards: List<CardImageView> = allCardsImages.filter { cards.contains(it.card) } //allCardsImages.subList(lastCardIndexDealt - numCards, lastCardIndexDealt)
        nextFiveCards.forEach { it.direction = direction }
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


        val delta = getOffset(cardImage, direction)
        val property = getProperty(direction)

        val animator = ObjectAnimator.ofFloat(cardImage, property, delta)

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
                //soundPlayer.start()
            }

        })
        animator.duration = 50
        hakemDetermination.hakemDealingAnimations.add(animator)

        hakemDetermination.cardZIndex++
        cardImage.translationZ = hakemDetermination.cardZIndex
        //hakemDetermination.hakemDealingAnimations.add(
         //       ObjectAnimator.ofFloat(cardImage, "translationZ", hakemDetermination.cardZIndex)
         //               .setDuration(10))
    }

    private fun getOffset(cardImage: CardImageView, direction: Direction): Float{
        cardImage.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        cardHeight = cardImage.measuredHeight
        cardWidth = cardImage.measuredWidth


        val displayMetrics = resources.displayMetrics
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val halfScreenWidth = (displayMetrics.widthPixels / 2).toFloat()
        var halfScreenHeight = (displayMetrics.heightPixels / 2).toFloat()
        //centerY -= 100f
        val verticalOffset = halfScreenHeight - cardHeight
        val horizontalOffset = halfScreenWidth - cardWidth

        return when (direction) {
            Direction.BOTTOM -> verticalOffset
            Direction.TOP -> -verticalOffset
            Direction.RIGHT -> horizontalOffset
            Direction.LEFT -> -horizontalOffset
        }
    }

    private fun getProperty(direction: Direction): String{
        return when (direction) {
            Direction.BOTTOM, Direction.TOP -> "translationY"
            Direction.LEFT, Direction.RIGHT -> "translationX"
        }
    }

    override fun hakemDetermined() {
        var set = AnimatorSet()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                ResetCardsInMiddle(false)
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

        supportActionBar?.hide()

        initMediaPlayer()

        init(true)

        game.determineHakem()

    }

    fun init(isHakemDetermination: Boolean = false){
        //if(isHakemDetermination){
            val scores = PrefsHelper.getCurrentScore()
            human_score.setText("your score: " + scores.first)
            opponent_score.setText("opponent score: " + scores.second)
        //}
        for(card: Card in game.deck.deck){
            var img  = CardImageView(card, this)
            root.addView(img)
            allCardsImages.add(img)
            if(isHakemDetermination) {
                hakemDetermination.cardsImages.add(img)
            }
            else{
//                img.setOnClickListener {
//                    if(game.isValidCard(card)) {
//                        playCardForwardAnim(it as CardImageView, card)
//                    }
//                    else{
//                        Toast.makeText(GameActivity@this, "not valid", Toast.LENGTH_SHORT).show()
//                    }
//                }
            }
        }
    }

    override fun cardPlayed(card: Card, direction: Direction) {
        val cardImg = findCardImage(card)
        playCardForwardAnim(cardImg, card)
    }

    private fun playCardForwardAnim(img: CardImageView, card:Card){
        if(img.direction == Direction.BOTTOM){
            removeClickListener(img)
        }
        img.setImageSource()
        val displayMetrics = resources.displayMetrics
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val centerX = (displayMetrics.widthPixels / 2).toFloat()
        var centerY = (displayMetrics.heightPixels / 2).toFloat()

        var animSet = AnimatorSet()

        //img.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val cardWidth = img.measuredWidth
        val cardHeight = img.measuredHeight
        val halfHeight = cardHeight / 2
        val halfWidth = cardWidth / 2

        var x = 0f
        var y = 0f
        when(img.direction){
            Direction.BOTTOM -> {
                x = centerX - halfWidth
                y = centerY - halfHeight
            }
            Direction.TOP -> {
                x = centerX - halfWidth
                y = centerY - cardHeight - halfHeight
            }
            Direction.RIGHT -> {
                x = centerX
                y = centerY - cardHeight
            }

            Direction.LEFT -> {
                x = centerX - cardWidth
                y = centerY - cardHeight
            }
        }
        val animator = ObjectAnimator.ofFloat(img, "x", x)
        val animator2 = ObjectAnimator.ofFloat(img, "y", y)
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                hands[img.direction.value].remove(img)
                //formHand(hands[img.directionHakemDetermination.value], img.directionHakemDetermination, false)
                game.cardPlayed(card, img.direction)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        animSet.playTogether(animator, animator2)
        animSet.start()


    //   val anim = AnimationUtils.loadAnimation(this, R.anim.push_up_in)
     //   anim.fillAfter = true
//       img.startAnimation(anim)
    }

    fun initMediaPlayer(){
        soundPlayer = MediaPlayer.create(this, R.raw.deal)
    }

    fun ResetCardsInMiddle(isHakemDetermination: Boolean) {
        clearAllCardsImages()
        init(isHakemDetermination)
    }

   private fun clearAllCardsImages(){
       for(image in allCardsImages){
           root.removeView(image)
       }
       allCardsImages.clear()
   }

    private fun setListenerForCard(card: CardImageView){
        card.setOnClickListener({

        })
    }


    private fun showHokmDialog(){
        val singleChoiceItems = resources.getStringArray(R.array.dialog_hokm)
        var itemSelected = 0
        var hokm = Suit.HEART
        AlertDialog.Builder(this)
                .setTitle("Select hokm")
                .setSingleChoiceItems(singleChoiceItems, itemSelected,
                        DialogInterface.OnClickListener {
                            dialogInterface, selectedIndex ->
                            when(selectedIndex){
                                0 -> hokm = Suit.HEART
                                1 -> hokm = Suit.SPADE
                                2 -> hokm = Suit.DIAMOND
                                3 -> hokm = Suit.CLUB
                            }

                        })
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, which ->
                    setHokmImage(hokm)
                    game.humanSpecifiedHokm(hokm)
                })
              //  .setNegativeButton("Cancel", null)
                .show()
    }

    private fun addClickListener(cardImage: CardImageView){
        cardImage.setOnClickListener {
            if (game.isValidCard(cardImage.card)) {
                playCardForwardAnim(it as CardImageView, cardImage.card)
            } else {
                Toast.makeText(GameActivity@ this, "not valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeClickListener(cardImage: CardImageView){
        cardImage.setOnClickListener(null)
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
