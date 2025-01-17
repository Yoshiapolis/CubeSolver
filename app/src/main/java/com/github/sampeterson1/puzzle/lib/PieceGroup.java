/*
 *	Stickers Twisty Puzzle Simulator and Solver
 *	Copyright (C) 2022 Sam Peterson <sam.peterson1@icloud.com>
 *	
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.sampeterson1.puzzle.lib;

import java.util.ArrayList;
import java.util.List;

import com.github.sampeterson1.puzzle.moves.Move;
import com.github.sampeterson1.puzzle.templates.GroupedPuzzle;

//Holds a list of pieces and coordinates them
public class PieceGroup {
	
	private GroupedPuzzle puzzle;
	private PieceBehavior behavior;
	private List<Piece> pieces;
	private List<Piece> movedPieces;
	private int position;
	private int puzzleSize;
	
	public PieceGroup(PieceBehavior behavior, GroupedPuzzle puzzle, int position) {
		this.position = position;
		this.pieces = new ArrayList<Piece>();
		this.movedPieces = new ArrayList<Piece>();
		this.puzzle = puzzle;
		this.puzzleSize = puzzle.getSize();
		this.behavior = behavior;
		
		int numPieces = behavior.getNumPieces(puzzleSize);
		for(int i = 0; i < numPieces; i ++) {
			Piece piece = behavior.createPiece(position, i);
			pieces.add(piece);
		}
	}
	
	public boolean isSolved() {
		for(Piece piece : pieces) {
			if(!piece.isSolved()) return false;
		}
		
		return true;
	}
	
	public boolean hasSolvedPiece() {
		for(Piece piece : pieces) {
			if(piece.isSolved()) return true;
		}
		
		return false;
	}
	
	public int getNumPieces() {
		return this.pieces.size();
	}
	
	public void setSolved(boolean solved) {
		for(Piece piece : pieces) {
			piece.setSolved(true);
		}
	}
	
	public void applyMoves() {
		for(Piece piece : movedPieces) {
			pieces.set(piece.getIndex(), piece);
		}
		movedPieces = new ArrayList<Piece>();
	}
	
	public void addMovedPiece(Piece piece) {
		this.movedPieces.add(piece);
	}
	
	public List<Piece> getAffectedPieces(Move move) {
		return behavior.getAffectedPieces(move, this);
	}
	
	public void makeMove(Move move) {
		List<Piece> toMove = null;
		if(move.isCubeRotation()) {
			toMove = pieces;
		} else {
			toMove = behavior.getAffectedPieces(move, this);
		}
		
		for(Piece piece : toMove) {
			behavior.movePiece(move, piece);
			PieceGroup group = puzzle.getGroup(piece.getType(), piece.getPosition());
			group.addMovedPiece(piece);
		}
	}
	
	public int getPuzzleSize() {
		return this.puzzleSize;
	}
	
	public List<Piece> getPieces() {
		return this.pieces;
	}
	
	public Piece getPiece() {
		return getPiece(0);
	}
	
	public Piece getPiece(int index) {
		return this.pieces.get(index);
	}
	
	public final int getPosition() {
		return this.position;
	}
	
}
