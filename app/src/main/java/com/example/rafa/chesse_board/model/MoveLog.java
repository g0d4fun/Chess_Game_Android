/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.rafa.chesse_board.model;

import com.example.rafa.chesse_board.model.board.Move;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author henri
 */
public class MoveLog implements Serializable {
    
    private List<Move> moves;

    public MoveLog() {
        moves = new ArrayList<>();
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void addMoves(Move move) {
        moves.add(move);
    }
    
    public int size(){
        return moves.size();
    }
    
    public void clear(){
        moves.clear();
    }
    
    public void removeMove(int index){
        moves.remove(index);
    }
    
    public void removeMove(Move index){
        moves.remove(index);
    }
}
