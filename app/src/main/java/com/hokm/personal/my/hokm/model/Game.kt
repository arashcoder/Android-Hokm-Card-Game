package com.hokm.personal.my.hokm.model

import android.util.Log
import com.hokm.personal.my.hokm.util.PrefsHelper

class Game(val animation: HakemAnimation){



    var deck = Deck()
    lateinit var hakem: Direction //= Direction.BOTTOM
    lateinit var tableDir: Direction //= Direction.BOTTOM
    lateinit var hokm: Suit //= Suit.SPADE
    var directionHakemDetermination: Direction = Direction.BOTTOM
    var hands: Array<MutableList<Card>> = Array(4){ mutableListOf<Card>() }
    var players: MutableList<Player> = mutableListOf()
    var teams: MutableList<Team> = mutableListOf()
    var table: MutableList<Card> = mutableListOf<Card>()
    var tableHistory: MutableList<MutableList<Card>> = mutableListOf()
    var lastIndex = deck.deck.lastIndex+1

    fun newGame(){
        this.hands.forEach { it.clear() }
        this.players.clear()
        this.teams.clear()
        this.table.clear()
        this.tableHistory.clear()
        lastIndex = deck.deck.lastIndex+1
        getNewDeck()
    }

    companion object {
        fun getTableWinner(table: MutableList<Card>, hokm: Suit, teams: MutableList<Team>): Player {
            val sameSuit =  table.all { it.suit == table[0].suit } // if all are of the same suit
            if(sameSuit) {
                return getWinner(table, teams)
            }
            else{
                val hokmCards = table.filter { it.suit == hokm}
                if(hokmCards.any()){
                    return getWinner(hokmCards, teams)
                }
                else{
                    // high card with the same suit as the first card wins.
                    var sameSuitAsFirstCard = table.filter { it.suit == table[0].suit }
                    return getWinner(sameSuitAsFirstCard, teams)
                }
            }
        }

        private fun getWinner(cards: List<Card>, teams: MutableList<Team>): Player{
            val winnerCard = cards.maxBy { it.value }
            val winnerPlayer = winnerCard?.player!!
            Log.e("WINNER ", winnerPlayer.name)
            //Log.e("CARDS ", cards)
            return winnerPlayer
        }
    }

    fun determineHakem(){
        var j = 0
        var card: Card = deck.getTopCard()
        animation.oneCardDealtForDeterminingHakem(card, directionHakemDetermination)


        while (card.value != Card.ACE){
            j++
            card = deck.getTopCard()
            Log.i("CARD", "card "+ card.value + " " + card.suit + " " + j)
            directionHakemDetermination = getNextDirection(directionHakemDetermination)
            animation.oneCardDealtForDeterminingHakem(card, directionHakemDetermination)
        }
        hakem = directionHakemDetermination
        tableDir = directionHakemDetermination
        animation.hakemDetermined()
        getNewDeck()
        Log.i("ACE", "card ace"+ card.value + " " + card.suit + " " + j)
    }

     fun getNextDirection(direction: Direction): Direction{
        return when(direction){
            Direction.BOTTOM -> Direction.RIGHT
            Direction.RIGHT -> Direction.TOP
            Direction.TOP -> Direction.LEFT
            Direction.LEFT -> Direction.BOTTOM
        }
    }

    fun isValidCard(card: Card): Boolean{
        if(tableDir != Direction.BOTTOM){
            return false
        }
        if(table.size == 0){
           return true
        }

        val hasSameSuit = players[Direction.BOTTOM.value].hand.any { it.suit == table[0].suit }
        if(!hasSameSuit){
            return true
        }

        if(table[0].suit == card.suit){
            return true
        }
        return false
    }


    fun dealCards(numCards: Int){

        var nextCards: Array<MutableList<Card>> = Array(4){ mutableListOf<Card>() }

        var dir = hakem
        for(i in 0 .. 3){
            nextCards[dir.value].addAll(deck.deck.subList(lastIndex-numCards, lastIndex))
            hands[dir.value].addAll(deck.deck.subList(lastIndex-numCards, lastIndex))
            lastIndex -= numCards
            dir = getNextDirection(dir)
        }

        val isHumanHakem = numCards == 5 && hakem == Direction.BOTTOM

        var hokm : Suit? = null
        if(numCards == 5){
            var humanPlayer = PlayerHuman("Arash", hands[Direction.BOTTOM.value])
            var rightAI = PlayerAI("right", Direction.RIGHT, hands[Direction.RIGHT.value])
            var topAI = PlayerAI("top", Direction.TOP, hands[Direction.TOP.value])
            var leftAI = PlayerAI("left", Direction.LEFT, hands[Direction.LEFT.value])

            players.add(Direction.BOTTOM.value, humanPlayer)
            players.add(Direction.RIGHT.value, rightAI)
            players.add(Direction.TOP.value, topAI)
            players.add(Direction.LEFT.value, leftAI)

            teams.add(0, Team(players[Direction.BOTTOM.value], players[Direction.TOP.value]))
            teams.add(1, Team(players[Direction.RIGHT.value], players[Direction.LEFT.value]))

            players[Direction.BOTTOM.value].team = teams[0]
            players[Direction.TOP.value].team = teams[0]
            players[Direction.RIGHT.value].team = teams[1]
            players[Direction.LEFT.value].team = teams[1]

                    if(!isHumanHakem){
                hokm = players[hakem.value].determineHokm()
                this.hokm = hokm
            }

        }
        else {
            players[Direction.BOTTOM.value].addHand(hands[Direction.BOTTOM.value])
            players[Direction.RIGHT.value].addHand(hands[Direction.RIGHT.value])
            players[Direction.TOP.value].addHand(hands[Direction.TOP.value])
            players[Direction.LEFT.value].addHand(hands[Direction.LEFT.value])
        }

        addPlayerToCard(hands[dir.value], players[dir.value])


        animation.cardsDealt(numCards, hakem, nextCards, isHumanHakem, hokm)
    }

    private fun addPlayerToCard(cards: MutableList<Card>, player: Player) {
        hands.forEachIndexed { index, hand -> hand.forEach { it.player = players[index] }}
        //cards.forEach { it.player = player }
    }

    fun playCard(){
        var direction = tableDir
        if(direction == Direction.BOTTOM)
            return

        val cardToPlay = players[direction.value].play(table, tableHistory, teams, hokm)
        animation.cardPlayed(cardToPlay , direction)
    }

    private fun getNewDeck(){
        deck = Deck()
    }

    fun humanSpecifiedHokm(hokm: Suit){
        this.hokm = hokm
        dealCards(4)
    }



    fun cardPlayed(card: Card, direction: Direction){
        players[direction.value].hand.remove(card)
        table.add(card)
        tableDir = getNextDirection(direction)

        if(table.size == 4){
            val winnerPlayer = getTableWinner(table, hokm, teams)//determineTableWinner()

            val winnerTeam = winnerPlayer.team
            winnerTeam.score++
            tableDir = winnerPlayer.direction
            PrefsHelper.saveTotalScore(winnerTeam.playerA is PlayerHuman)
            val isGameOver = winnerTeam.score == 7
            var wholeSetOver = false
            if(isGameOver){
                //val scores = PrefsHelper.getCurrentScore()
                //if(scores.first == 2 || scores.second == 2){
                //    wholeSetOver = true
                //}
               if(winnerTeam != players[hakem.value].team){
                   hakem = getNextDirection(hakem)
                   tableDir = hakem
               }
            }

            animation.tableComplete(winnerTeam.playerA?.direction, winnerTeam.score, table, isGameOver, wholeSetOver)
            tableHistory.add(table)
            table.clear()
        }
        else{
            playCard()
        }


    }

//     fun determineTableWinner(): Team {
//       val sameSuit = table.all { it.suit == table[0].suit } // if all are of the same suit
//        if(sameSuit) {
//           return getWinner(table)
//        }
//        else{
//            val hokmCards = table.filter { it.suit == hokm}
//            if(hokmCards.any()){
//               return getWinner(hokmCards)
//            }
//            else{
//                // high card with the same suit as the first card wins.
//                var sameSuitAsFirstCard = table.filter { it.suit == table[0].suit }
//                return getWinner(sameSuitAsFirstCard)
//            }
//        }
//    }
//
//    private fun getWinner(cards: List<Card>): Team{
//        val winnerCard = cards.maxBy { it.value }
//        val winnerPlayer = winnerCard?.player
//        val winnerTeam = teams.first { it.playerA == winnerPlayer || it.playerB == winnerPlayer }//TODO: check this comparison
//        return winnerTeam
//    }

}



enum class Direction(val value: Int) {
    BOTTOM(0),
    RIGHT(1),
    TOP(2),
    LEFT(3),
}

interface HakemAnimation{
     fun oneCardDealtForDeterminingHakem(card: Card, direction: Direction)
    fun hakemDetermined()
    //fun hokmDetermined()
    fun cardsDealt(numCards: Int, hakem: Direction, hands: Array<MutableList<Card>>, shouldDetermineHokm: Boolean, hokm: Suit?=null)
    fun cardPlayed(card: Card, direction: Direction)
    fun tableComplete(winnerDirection: Direction?, winnerScore: Int, tableCards: List<Card>, isGameOver: Boolean, setOver: Boolean)
}