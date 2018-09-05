package com.hokm.personal.my.hokm.model

import java.util.*

class PlayerAI(name: String, direction: Direction, cards: MutableList<Card>): Player(name, cards) {
    override var direction: Direction = direction

    override fun play(table: MutableList<Card>,
                      tableHistory: MutableList<MutableList<Card>>,
                      teams: MutableList<Team>,
                      hokm: Suit): Card{
        if(!table.any()){// this player must play the first card for this round
            //if(tableHistory.size == 0){//first round
                val notHokms = hand.filter { it.suit != hokm }
                return  if(notHokms.any()) notHokms.maxBy { it.value }!! else hand[0]
            //}
        }
        var sameSuitCards = hand.filter { it.suit == table[0].suit }
        if(sameSuitCards.any()){// must play one of the same suit cards
            sameSuitCards = sameSuitCards.sortedWith(compareBy{it.value})
            if(table.size == 1){
                return sameSuitCards.first()// if this is the second player, return the lowest card
            }
            if(table.size == 2) {
                val ourTeam = this.team
                val winnerPlayer = Game.getTableWinner(table, hokm, teams)
                if (winnerPlayer.team == ourTeam) {// we are this round's winner, so play tafraghe
                    val teamMateVal = table[0].value
                    if (teamMateVal == 12 || teamMateVal == 13 || teamMateVal == 14) {
                        return sameSuitCards.first()
                    }
                }
                return sameSuitCards.last()
            }
            if(table.size == 3) {
                val ourTeam = this.team
                val winnerPlayer = Game.getTableWinner(table, hokm, teams)
                if(winnerPlayer.team == ourTeam){// we are this round's winner, so play the lowest card
                    return sameSuitCards.first()
                }
                else{
                    // if we can win with the highest card, play it, otherwise return the lowest card
                    val tableHighestCard = table.maxBy { it.value }
                    val highestValue = tableHighestCard?.value ?: 0
                    if(sameSuitCards.last().value > highestValue){
                        return sameSuitCards.last()
                    }
                    return sameSuitCards.first()
                }
            }
        }


        else{// no same suit card
            if(table.size == 1){
                if(table[0].suit == hokm){// must play tafraghe
                    return tafraghe(hokm)!!
                }
                else{
                    return bebor(hokm)!!
                }
            }

            if(table.size == 2){
                if(table[0].suit == hokm){// must play tafraghe
                    return tafraghe(hokm)!!
                }
                else{
                    val ourTeam = this.team
                    val winnerPlayer = Game.getTableWinner(table, hokm, teams)
                    if (winnerPlayer.team == ourTeam) {// we are this round's winner, so play tafraghe
                        val teamMateVal = table[0].value
                        if(teamMateVal == 12 || teamMateVal == 13 || teamMateVal == 14){
                            return tafraghe(hokm)!!
                        }
                        return bebor(hokm)!!
                    }
                    else{
                        val maxHokmOnTable = table.filter { it.suit == hokm }.maxBy { it.value }
                        val myMaxHokm = hand.filter { it.suit == hokm }.maxBy { it.value }

                        if(maxHokmOnTable == null){
                            return bebor(hokm)!!
                        }

                        if(myMaxHokm != null && myMaxHokm.value > maxHokmOnTable.value){
                            return myMaxHokm
                        }
                        return tafraghe(hokm)!!
                    }
                }
            }

            if(table.size == 3){
                if(table[0].suit == hokm){// must play tafraghe
                    return tafraghe(hokm)!!
                }
                else {
                    val ourTeam = this.team
                    val winnerPlayer = Game.getTableWinner(table, hokm, teams)
                    if (winnerPlayer.team == ourTeam) {// we are this round's winner, so play tafraghe
                        return tafraghe(hokm)!!
                    } else {
                        val maxHokmOnTable = table.filter { it.suit == hokm }.maxBy { it.value }
                        val myMaxHokm = hand.filter { it.suit == hokm }.maxBy { it.value }

                        if (maxHokmOnTable == null) {
                            return bebor(hokm)!!
                        }

                        if (myMaxHokm != null && myMaxHokm.value > maxHokmOnTable.value) {
                            return myMaxHokm
                        }
                        return tafraghe(hokm)!!

                    }
                }
            }
        }

        return hand[Random().nextInt(hand.size)]// this should be removed
    }

    private fun tafraghe(hokm: Suit): Card?{
        return hand.filter { it.suit != hokm }.minBy { it.value }
    }

    private fun bebor(hokm: Suit): Card?{
       val hokmCards = hand.filter { it.suit == hokm }
        if(hokmCards.any()){
            return hokmCards.minBy { it.value }
        }
        return tafraghe(hokm)
    }


}