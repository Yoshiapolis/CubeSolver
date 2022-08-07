/*
    PrimePuzzle Twisty Puzzle Simulator and Solver
    Copyright (C) 2022 Sam Peterson
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.github.yoshiapolis.cube.solvers;

import com.github.yoshiapolis.cube.pieces.Cube;
import com.github.yoshiapolis.cube.util.CubeMoveUtil;
import com.github.yoshiapolis.puzzle.lib.Algorithm;
import com.github.yoshiapolis.puzzle.lib.Color;
import com.github.yoshiapolis.puzzle.lib.Axis;
import com.github.yoshiapolis.puzzle.lib.Piece;

public class OLLCase {

	private Algorithm solution;
	int[] edgeLocations;
	int[] cornerLocations;

	public OLLCase(String solution, int[] locations) {
		this.solution = CubeMoveUtil.parseAlgorithm(solution);
		cornerLocations = new int[4];
		for (int i = 0; i < 8; i += 2)
			cornerLocations[i / 2] = locations[i];
		edgeLocations = new int[4];
		for (int i = 1; i < 8; i += 2)
			edgeLocations[i / 2] = locations[i];
	}

	public Algorithm getSolution() {
		return this.solution;
	}

	public boolean recognize(Cube cube) {
		Color top = cube.getCenterColor(Axis.U);
		boolean retVal = true;
		if (cube.getSize() > 2) {
			for (int i = 0; i < 4; i++) {
				Piece piece = cube.getEdge(i).getPiece(0);
				if (piece.indexOfColor(top) != edgeLocations[i]) {
					retVal = false;
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			Piece piece = cube.getCorner(i).getPiece();
			if (piece.indexOfColor(top) != cornerLocations[i]) {
				retVal = false;
			}
		}

		return retVal;
	}
}
