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

package com.github.sampeterson1.puzzles.cube.display;

import com.github.sampeterson1.math.Matrix3D;
import com.github.sampeterson1.puzzle.display.DisplayPiece;
import com.github.sampeterson1.puzzle.lib.Color;
import com.github.sampeterson1.puzzle.lib.Piece;
import com.github.sampeterson1.puzzle.lib.PieceType;
import com.github.sampeterson1.puzzle.moves.Algorithm;
import com.github.sampeterson1.puzzle.moves.InvalidAlgorithmException;
import com.github.sampeterson1.puzzles.cube.util.CubeAlgorithmUtil;
import com.github.sampeterson1.renderEngine.loaders.OBJLoader;
import com.github.sampeterson1.renderEngine.models.ColoredMesh;

//An implementation of DisplayPiece that represents a piece on a cube
public class CubeDisplayPiece extends DisplayPiece {
	
	private static final float CUBE_DRAW_SIZE = 20;
	
	/*
	 * These algorithms take pieces at their "origin positions" (see getOrigin methods below)
	 * and move them to their correct positions and orientations
	 */
	private static final Algorithm[] cornerRotations = initCornerRotations();
	private static final Algorithm[] edgeRotations = initEdgeRotations();
	private static final Algorithm[] centerRotations = initCenterRotations();
	
	private static ColoredMesh cornerPieceMesh;
	private static ColoredMesh edgePieceMesh;
	private static ColoredMesh centerPieceMesh;
	
	private float pieceSize;
	
	private static final Algorithm[] initCornerRotations() {
		Algorithm[] cornerRotations = new Algorithm[8];
		
		try {
			cornerRotations[0] = CubeAlgorithmUtil.parseAlgorithm("R F'");
			cornerRotations[1] = CubeAlgorithmUtil.parseAlgorithm("F'");
			cornerRotations[2] = CubeAlgorithmUtil.parseAlgorithm("F' U'");
			cornerRotations[3] = CubeAlgorithmUtil.parseAlgorithm("F' U2");
			cornerRotations[4] = CubeAlgorithmUtil.parseAlgorithm("D'");
			cornerRotations[5] = CubeAlgorithmUtil.parseAlgorithm("");
			cornerRotations[6] = CubeAlgorithmUtil.parseAlgorithm("D");
			cornerRotations[7] = CubeAlgorithmUtil.parseAlgorithm("D2");
		} catch (InvalidAlgorithmException e) {
			e.printStackTrace();
		}

		return cornerRotations;
	}
	
	private static final Algorithm[] initEdgeRotations() {
		Algorithm[] edgeRotations = new Algorithm[12];
		
		try {
			edgeRotations[0] = CubeAlgorithmUtil.parseAlgorithm("F2");
			edgeRotations[1] = CubeAlgorithmUtil.parseAlgorithm("F2 U'");
			edgeRotations[2] = CubeAlgorithmUtil.parseAlgorithm("F2 U2");
			edgeRotations[3] = CubeAlgorithmUtil.parseAlgorithm("F2 U");
			edgeRotations[4] = CubeAlgorithmUtil.parseAlgorithm("F");
			edgeRotations[5] = CubeAlgorithmUtil.parseAlgorithm("F U'");
			edgeRotations[6] = CubeAlgorithmUtil.parseAlgorithm("F U2");
			edgeRotations[7] = CubeAlgorithmUtil.parseAlgorithm("F U");
			edgeRotations[8] = CubeAlgorithmUtil.parseAlgorithm("");
			edgeRotations[9] = CubeAlgorithmUtil.parseAlgorithm("D");
			edgeRotations[10] = CubeAlgorithmUtil.parseAlgorithm("D2");
			edgeRotations[11] = CubeAlgorithmUtil.parseAlgorithm("D'");
		} catch (InvalidAlgorithmException e) {
			e.printStackTrace();
		}
		
		return edgeRotations;
	}
	
	private static final Algorithm[] initCenterRotations() {
		Algorithm[] centerRotations = new Algorithm[6];
		
		try {
			centerRotations[0] = CubeAlgorithmUtil.parseAlgorithm("U'");
			centerRotations[1] = CubeAlgorithmUtil.parseAlgorithm("R");
			centerRotations[2] = CubeAlgorithmUtil.parseAlgorithm("");
			centerRotations[3] = CubeAlgorithmUtil.parseAlgorithm("U");
			centerRotations[4] = CubeAlgorithmUtil.parseAlgorithm("R'");
			centerRotations[5] = CubeAlgorithmUtil.parseAlgorithm("U2");
		} catch (InvalidAlgorithmException e) {
			e.printStackTrace();
		}
		
		return centerRotations;
	}
	
	public CubeDisplayPiece(Piece position) {
		super(position);
		
		if(cornerPieceMesh == null || cornerPieceMesh.getData().isDeleted())
			loadMeshes();
	}
	
	private void loadMeshes() {
		cornerPieceMesh = OBJLoader.loadColoredMesh("cube/Corner.obj");
		edgePieceMesh = OBJLoader.loadColoredMesh("cube/Edge.obj");
		centerPieceMesh = OBJLoader.loadColoredMesh("cube/Center.obj");
	}
	
	/*
	 * Return a matrix representing the world position of a corner piece at
	 * the bottom right corner of the front face. This position is the "origin"
	 * at which every corner piece will be placed initially.
	 */
	private Matrix3D getCornerOrigin() {
		float positionCoord = (CUBE_DRAW_SIZE - pieceSize) / 2;
		
		Matrix3D translation = new Matrix3D();
		translation.translate(positionCoord, -positionCoord, positionCoord);
		
		return translation;
	}
	
	/*
	 * Return a matrix representing the world position of an edge piece at
	 * the bottom edge of the front face. This position is the "origin"
	 * at which every edge piece will be placed initially.
	 */
	private Matrix3D getEdgeOrigin(Piece piece) {
		int edgeSize = piece.getPuzzleSize() - 2;
		int index = piece.getIndex();
		
		//the distance from the center of the cube to the center of the edge piece
		float displacement = (CUBE_DRAW_SIZE - pieceSize) / 2;
		
		//the x position of an edge piece with index 0
		float edgeXOrigin = -(pieceSize - edgeSize * pieceSize) / 2;
		
		float xPos = edgeXOrigin - index * pieceSize;

		Matrix3D translation = new Matrix3D();
		translation.translate(xPos, -displacement, displacement);
		
		return translation;
	}
	
	/*
	 * Return a matrix representing the world position of a center piece on the front face.
	 * This position is the "origin" at which every center piece will be placed initially.
	 */
	private Matrix3D getCenterOrigin(Piece piece) {
		int centerSize = piece.getPuzzleSize() - 2;
		
		//split the 1-dimensional center index into an x and y index
		int index = piece.getIndex();
		int xIndex = index % centerSize;
		int yIndex = index / centerSize;
		
		//the distance from the center of the cube to the center of the center piece
		float displacement = (CUBE_DRAW_SIZE - pieceSize) / 2;
		
		//the position of a center piece with index 0
		float centerStart = (pieceSize - centerSize * pieceSize) / 2;
		
		//use the x and y indices to calculate the position of the piece
		float xCoord = centerStart + pieceSize * xIndex;
		float yCoord = -centerStart - pieceSize * yIndex;
		
		Matrix3D translation = new Matrix3D();
		translation.translate(xCoord, yCoord, displacement);
		
		return translation;
	}
	
	//Apply the rotation algorithms into a rotation matrix
	private Matrix3D getPieceRotation(Piece piece) {
		PieceType type = piece.getType();
		int position = piece.getPosition();

		Algorithm alg = null;
		if(type == PieceType.CENTER) {
			alg = centerRotations[position];
		} else if(type == PieceType.EDGE) {
			alg = edgeRotations[position];
		} else if(type == PieceType.CORNER) {
			alg = cornerRotations[position];
		}
		
		return Algorithm.getRotationFromAlgorithm(alg);
	}
	
	@Override
	protected void setColors() {
		Piece piece = super.getPiece();
		PieceType type = piece.getType();
		super.setColor("Border", Color.WHITE);
		
		if(type == PieceType.EDGE) {
			super.setColor("Bottom", piece.getColor(0));
			super.setColor("Front", piece.getColor(1));
		} else if(type == PieceType.CORNER) {
			super.setColor("Front", piece.getColor(0));
			super.setColor("Right", piece.getColor(1));
			super.setColor("Bottom", piece.getColor(2));
		} else if(type == PieceType.CENTER) {
			super.setColor("Front", piece.getColor(0));
		}
	}
	
	@Override
	protected Matrix3D getWorldPosition() {
		Piece piece = super.getPiece();
		pieceSize = CUBE_DRAW_SIZE / piece.getPuzzleSize();
		Matrix3D transformation = new Matrix3D();
		transformation.scale(pieceSize / 2);
		
		PieceType type = piece.getType();		
		if(type == PieceType.EDGE) {
			transformation.multiply(getEdgeOrigin(piece));
		} else if(type == PieceType.CORNER) {
			transformation.multiply(getCornerOrigin());
		} else if(type == PieceType.CENTER) {
			transformation.multiply(getCenterOrigin(piece));
		}
		
		transformation.multiply(getPieceRotation(piece));
		return transformation;
	}
	
	@Override
	public ColoredMesh getMesh() {
		ColoredMesh mesh = null;
		Piece piece = super.getPiece();
		
		if(piece.getType() == PieceType.CORNER) {
			return cornerPieceMesh;
		} else if(piece.getType() == PieceType.EDGE) {
			return edgePieceMesh;
		} else if(piece.getType() == PieceType.CENTER) {
			return centerPieceMesh;
		}

		return mesh;
	}

}
