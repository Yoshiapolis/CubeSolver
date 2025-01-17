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

package com.github.sampeterson1.puzzles.cube.solvers;

import java.util.ArrayList;

import com.github.sampeterson1.puzzle.moves.Axis;
import com.github.sampeterson1.puzzle.moves.Move;
import com.github.sampeterson1.puzzles.cube.meta.Cube;

/*
 * This class is an implementation of the full PLL step of the CFOP method
 * with an extra step to solve parity on big cubes.
 * 
 * Algorithms from https://jperm.net/algs/pll
 */
public class PLLSolver {

	/*
	 * Shortcuts to make implementing each case easier.
	 * These color names correspond with the colors seen on the referenced website.
	 */
	private static final Axis B = Axis.F;
	private static final Axis R = Axis.R;
	private static final Axis G = Axis.B;
	private static final Axis O = Axis.L;
	
	private Cube cube;
	private ArrayList<PLLCase> cases;

	public PLLSolver(Cube cube) {
		this.cube = cube;
		cases = new ArrayList<PLLCase>();

		addCase("x L2 D2 L' U' L D2 L' U L' x'", new Axis[] { O, B, R, R, B, G, G, O });
		addCase("x' L2 D2 L U L' D2 L U' L x", new Axis[] { R, B, O, R, G, G, B, O });
		addCase("R' U' F' R U R' U' R' F R2 U' R' U' R U R' U R", new Axis[] { B, G, G, R, R, B, O, O });
		addCase("R2 U R' U R' U' R U' R2 U' D R' U R D'", new Axis[] { B, R, G, O, R, B, O, G });
		addCase("R' U' R U D' R2 U R' U R U' R U' R2 D", new Axis[] { B, G, G, B, R, O, O, R });
		addCase("R2 U' R U' R U R' U R2 U D' R U' R' D", new Axis[] { B, G, G, O, R, R, O, B });
		addCase("R U R' U' D R2 U' R U' R' U R' U R2 D'", new Axis[] { B, O, G, G, R, B, O, R });
		addCase("x R2 F R F' R U2 r' U r U2 x'", new Axis[] { B, B, G, G, R, R, O, O });
		addCase("R U R' F' R U R' U' R' F R2 U' R'", new Axis[] { O, B, R, O, B, R, G, G });
		addCase("R U' R' U' R U R D R' U' R D' R' U2 R'", new Axis[] { O, O, R, B, B, G, G, R });
		addCase("R2 F R U R U' R' F' R U2 R' U2 R", new Axis[] { R, B, O, G, G, O, B, R });
		addCase("R U R' U' R' F R2 U' R' U' R U R' F'", new Axis[] { B, B, G, O, R, G, O, R });
		addCase("x' L' U L D' L' U' L D L' U' L D' L' U L D x", new Axis[] { O, B, G, R, R, G, B, O });
		addCase("z U R' D R2 U' R D' U R' D R2 U' R D' z' U'", new Axis[] { G, B, R, O, B, G, O, R });
		addCase("R' U R U' R' F' U' F R U R' F R' F' R U' R", new Axis[] { B, B, O, O, G, G, R, R });
		addCase("R' U R' U' y R' F' R2 U' R' U R' F R F y'", new Axis[] { B, B, O, G, G, R, R, O });
		addCase("F R U' R' U' R U R' F' R U R' U' R' F R F'", new Axis[] { B, B, O, R, G, O, R, G });
		addCase("M2 U M2 U2 M2 U M2", new Axis[] { B, G, R, O, G, B, O, R });
		addCase("R U' R U R U R U' R' U' R2", new Axis[] { B, R, R, O, G, G, O, B });
		addCase("R2 U R U R' U' R' U' R' U R'", new Axis[] { B, O, R, B, G, G, O, R });
		addCase("M' U M2 U M2 U M' U2 M2", new Axis[] { O, G, B, R, R, B, G, O });
		addCase("", new Axis[] { B, B, R, R, G, G, O, O });
	}

	public void solve() {
		System.out.println("Solving PLL...");
		cube.pushRotations();
		
		if(!solvePLL()) {
			//If we cannot find the correct PLL case, we must have parity
			PLLParity();
			solve();
		}

		cube.popRotations();
	}

	private void addCase(String alg, Axis[] faces) {
		cases.add(new PLLCase(alg, faces));
	}

	//Rotate the cube and align the U face to recognize and solve PLL
	private boolean solvePLL() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (PLLCase c : cases) {
					if (c.recognize(cube)) {
						cube.executeAlgorithm(c.getSolution());
						return true;
					}
				}
				cube.makeMove(new Move(Axis.U, true));
			}
			cube.makeRotation(Axis.U, true);
		}

		return false;
	}

	//Execute the PLL parity algorithm
	private void PLLParity() {
		cube.makeMove(new Move(Axis.R, false));
		cube.makeMove(new Move(Axis.U, true));
		cube.makeMove(new Move(Axis.R, true));
		cube.makeMove(new Move(Axis.U, false));

		int cubeSize = cube.getSize();

		for (int i = 1; i < cubeSize / 2; i++) {
			cube.makeMove(new Move(Axis.R, i, true).repeated(2));
		}

		cube.makeMove(new Move(Axis.U, true).repeated(2));

		for (int i = 1; i < cubeSize / 2; i++) {
			cube.makeMove(new Move(Axis.R, i, true).repeated(2));
		}

		for (int i = 0; i < cubeSize / 2; i++) {
			cube.makeMove(new Move(Axis.U, i, true).repeated(2));
		}

		for (int i = 1; i < cubeSize / 2; i++) {
			cube.makeMove(new Move(Axis.R, i, true).repeated(2));
		}

		for (int i = 1; i < cubeSize / 2; i++) {
			cube.makeMove(new Move(Axis.U, i, true).repeated(2));
		}

		cube.makeMove(new Move(Axis.U, true));
		cube.makeMove(new Move(Axis.R, false));
		cube.makeMove(new Move(Axis.U, false));
		cube.makeMove(new Move(Axis.R, true));
	}

}
