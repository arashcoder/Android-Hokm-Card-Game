package com.hokm.personal.my.hokm.model

abstract class Player {
    val name: String
    val hand: MutableList<Card> = mutableListOf()
    lateinit var team: Team

    internal abstract var direction: Direction
    protected var toLeft: Int = 0
    protected var toRight: Int = 0
    protected var toAcross: Int = 0

    constructor(name: String, hand: MutableList<Card>) {
        this.name = name
        //this.hand = hand
        addHand(hand)
    }

     fun addHand(newHand: MutableList<Card>) {
         hand.clear()
         //newHand.forEach { it.player = this }
         hand.addAll(newHand)
    }

    abstract fun play(table: MutableList<Card>,
                      tableHistory: MutableList<MutableList<Card>>,
                      teams: MutableList<Team>,
                      hokm: Suit): Card
    fun determineHokm(): Suit {
        var groupWithCounts = hand.groupingBy { it.suit }.eachCount()
       var sortedHandByCount = groupWithCounts.toList().sortedByDescending { (key, value) -> value }
       // var counts = hand.sortWith(compareBy{it.suit}.thenBy)
       if(sortedHandByCount[0].second >= 3){
           return sortedHandByCount[0].first
       }
        if(sortedHandByCount[0].second > sortedHandByCount[1].second){
            return sortedHandByCount[0].first
        }
        else{
             val first= hand.filter { it.suit == sortedHandByCount[0].first }.maxBy { it.value }
            val second = hand.filter { it.suit == sortedHandByCount[1].first }.maxBy { it.value }
            return if(first?.value!! > second?.value!!){
                sortedHandByCount[0].first
            } else{
                sortedHandByCount[1].first
            }
        }
    }


}