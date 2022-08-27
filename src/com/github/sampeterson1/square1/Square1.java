package com.github.sampeterson1.square1;

import java.util.List;

import javax.management.remote.SubjectDelegationPermission;

import com.github.sampeterson1.math.Mathf;
import com.github.sampeterson1.puzzle.display.ColorPalette;
import com.github.sampeterson1.puzzle.display.DisplayPiece;
import com.github.sampeterson1.puzzle.lib.Algorithm;
import com.github.sampeterson1.puzzle.lib.Axis;
import com.github.sampeterson1.puzzle.lib.Color;
import com.github.sampeterson1.puzzle.lib.Move;
import com.github.sampeterson1.puzzle.lib.Piece;
import com.github.sampeterson1.puzzle.lib.PieceType;
import com.github.sampeterson1.puzzle.lib.PuzzleType;
import com.github.sampeterson1.puzzle.lib.SimplePuzzle;

public class Square1 extends SimplePuzzle {

	private static final int NUM_CENTERS = 2;
	private static final int[] EDGE_POSITIONS = new int[] {0, 3, 6, 9, 12, 15, 18, 21};
	private static final int[] CORNER_POSITIONS = new int[] {1, 4, 7, 10, 13, 16, 19, 22};
	
	private Square1CSSolver cubeShapeSolver;
	private Square1OCSolver ocSolver;
	private Square1OESolver oeSolver;
	
	public Square1() {
		super(1, true);
		
		super.createPieces(new Square1CenterBehavior(this), NUM_CENTERS);
		super.createPieces(new Square1EdgeBehavior(this), EDGE_POSITIONS);
		super.createPieces(new Square1CornerBehavior(this), CORNER_POSITIONS);
		
		this.cubeShapeSolver = new Square1CSSolver(this);
		this.ocSolver = new Square1OCSolver(this);
		this.oeSolver = new Square1OESolver(this);
	}
	
	public Piece getPiece(int position) {
		Piece edge = getEdge(position);
		
		if(edge == null) return getCorner(position);
		return edge;
	}
	
	public Piece getCenter(int position) {
		return super.getPiece(PieceType.CENTER, position);
	}
	
	public Piece getEdge(int position) {
		return super.getPiece(PieceType.EDGE, position);
	}
	
	public Piece getCorner(int position) {
		return super.getPiece(PieceType.CORNER, position);
	}
	
	@Override
	public Axis transposeAxis(Axis face) {
		return face;
	}

	@Override
	public Algorithm parseAlgorithm(String algStr) {
		return Square1Util.parseAlgorithm(algStr);
	}

	@Override
	public Algorithm simplify(Algorithm alg) {
		return Square1Util.simplify(alg);
	}

	@Override
	public Algorithm scramble(int length) {
		super.setLogMoves(true);
		super.clearMoveLog();
		
		for(int i = 0; i < length; i ++) {
			int top = (int) Mathf.random(0, 7);
			int bottom = (int) Mathf.random(0, 7);
			boolean topCW = (Mathf.random(0, 1) > 0.5f);
			boolean bottomCW = (Mathf.random(0, 1) > 0.5f);
			
			for(int j = 0; j < top; j ++) super.makeMove(new Move(Axis.SU, topCW));
			for(int j = 0; j < bottom; j ++) super.makeMove(new Move(Axis.SD, bottomCW));
			while(Square1Util.topLocked(this)) super.makeMove(new Move(Axis.SU, true));
			while(Square1Util.bottomLocked(this)) super.makeMove(new Move(Axis.SD, true));
			super.makeMove(new Move(Axis.S1, true));
		}
		
		return super.getMoveLog();
	}

	@Override
	public Algorithm solve() {
		
		Algorithm solution = new Algorithm();
		
		for(int i = 0; i < 1000; i ++) {
			solution.append(this.scramble(30));
			solution.append(this.actualSolve());
		}
		
		//solution.append(ocSolver.solve());
		//solution.append(oeSolver.solve());
		
		return this.simplify(solution);
		
		
		//return actualSolve();
	}

	private Algorithm actualSolve() {
		Algorithm solution = cubeShapeSolver.solve();
		solution.append(ocSolver.solve());
		solution.append(oeSolver.solve());
		
		return solution;
	}
	
	@Override
	public ColorPalette createDefaultColorPalette() {
		ColorPalette palette = new ColorPalette();
		
		palette.putColor(Color.BORDER);
		palette.putColor(Color.WHITE);
		palette.putColor(Color.YELLOW);
		palette.putColor(Color.ORANGE);
		palette.putColor(Color.BLUE);
		palette.putColor(Color.GREEN);
		palette.putColor(Color.RED);
		
		return palette;
	}

	@Override
	public DisplayPiece createDisplayPiece(Piece piece) {
		return new Square1DisplayPiece(piece);
	}

	@Override
	public PuzzleType getType() {
		return PuzzleType.SQUARE1;
	}

}

/*

 CORNER
CORNER
CORNER
EDGE
EDGE
EDGE
CORNER
EDGE

CORNER
EDGE
EDGE
CORNER
CORNER
EDGE
EDGE
CORNER





EDGE
CORNER
CORNER
EDGE
EDGE
EDGE
CORNER
CORNER

EDGE
EDGE
EDGE
CORNER
EDGE
CORNER
CORNER
CORNER
 
 
 
 
 CORNER
EDGE
CORNER
EDGE
EDGE
CORNER
CORNER
EDGE

EDGE
EDGE
CORNER
CORNER
CORNER
EDGE
EDGE
CORNER
 
*/
