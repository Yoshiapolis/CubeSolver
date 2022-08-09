package com.github.sampeterson1.pyraminx.pieces;

import java.util.ArrayList;
import java.util.List;

import com.github.sampeterson1.puzzle.lib.Axis;
import com.github.sampeterson1.puzzle.lib.Move;
import com.github.sampeterson1.puzzle.lib.Piece;
import com.github.sampeterson1.puzzle.lib.PieceBehavior;
import com.github.sampeterson1.puzzle.lib.PieceGroup;
import com.github.sampeterson1.puzzle.lib.PieceType;
import com.github.sampeterson1.pyraminx.util.PyraminxCenterUtil;
import com.github.sampeterson1.pyraminx.util.PyraminxMoveUtil;

public class PyraminxCenterBehavior implements PieceBehavior {

	private static PieceType type = PieceType.CENTER;
	
	@Override
	public Piece createPiece(int position, int index, int puzzleSize) {
		Piece center = new Piece(type, position, index, puzzleSize);
		center.setColor(PyraminxCenterUtil.getColor(position));
		
		return center;
	}

	@Override
	public List<Piece> getAffectedPieces(Move move, PieceGroup group) {

		Axis face = Pyraminx.faces[group.getPosition()];
		Axis pivot = move.getFace();
		
		int layer = move.getLayer();
		int puzzleSize = group.getPuzzleSize();
		
		List<Piece> pieces = new ArrayList<Piece>();
		if(face == pivot) {
			if(move.getLayer() == 0)
				return group.getPieces();
		} else if(layer > 0 && layer < puzzleSize - 2) {
			int rotation = PyraminxCenterUtil.getRotationOffset(pivot, face);
			int centerSize = group.getPuzzleSize() - 3;
			int sqrSize = centerSize * centerSize;
			
			int invLayer = centerSize - move.getLayer() + 1;
			int lowerIndex = sqrSize - invLayer * invLayer;
			invLayer --;
			int upperIndex = sqrSize - (invLayer * invLayer);
			
			for(int i = lowerIndex; i < upperIndex; i ++) {
				int index = i;
				if(rotation < 0) {
					for(int j = 0; j < -rotation; j ++)
						index = PyraminxCenterUtil.rotateIndexCCW(index, puzzleSize - 3);
				} else if(rotation > 0) {
					for(int j = 0; j < rotation; j ++)
						index = PyraminxCenterUtil.rotateIndexCW(index, puzzleSize - 3);
				}
				
				pieces.add(group.getPiece(index));
			}
		}
		
		return pieces;
	}

	@Override
	public void movePiece(Move move, Piece piece) {
		int puzzleSize = piece.getPuzzleSize();
		Axis currentFace = Pyraminx.faces[piece.getPosition()];
		
		int newIndex = PyraminxCenterUtil.mapIndex(move, currentFace, piece.getIndex(), puzzleSize);
		Axis newFace = PyraminxMoveUtil.mapFace(currentFace, move);
		
		piece.setIndex(newIndex);
		piece.setPosition(Pyraminx.getAxisIndex(newFace));
	}

	@Override
	public int getNumPieces(int puzzleSize, int position) {
		int a = puzzleSize - 3;
		return a*a;
	}

	@Override
	public PieceType getType() {
		return type;
	}
}