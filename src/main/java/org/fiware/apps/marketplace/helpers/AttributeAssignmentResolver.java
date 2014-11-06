package org.fiware.apps.marketplace.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class to resolve the problem of n:m attribute assignments.
 * 
 * @author D058352
 * 
 */
public abstract class AttributeAssignmentResolver {

	/**
	 * Generates an array containing the indices with the optimal attribute assignments to maximize similarity.
	 * 
	 * @param values Array of similarity values with source attributes as rows and target attributes as columns. Values[0][0] e.g. contains
	 *            similarity of source attribute with index 0 compared to target attribute with index 0.
	 * @return Returns an array with an entry for each row of values[][]. The index of the returned array represents the index of the source
	 *         attribute and the corresponding value is the index of the chosen target attribute. When the value of an index in the returned
	 *         array is '-1', no assignment was made for this source attribute.
	 */
	public static int[] getMaximalAttributeAssignments(double[][] values) {
		// values must be nxn!
		return reduceArray(HungarianAlgorithm.findMatching(copyAsQuadraticArray(values)), values.length, values[0].length);
	}

	/**
	 * Clones the given array with quadratic dimensions and fills the new cells with the max contained value.
	 * 
	 * @param values
	 * @return
	 */
	protected static double[][] copyAsQuadraticArray(double[][] values) {
		double maxVal = Double.MIN_VALUE;
		int maxD1 = values.length;
		int maxD2 = 0;
		for (int row = 0; row < values.length; row++) {
			maxD2 = Math.max(maxD2, values[row].length);
			for (int col = 0; col < values[row].length; col++) {
				maxVal = Math.max(values[row][col], maxVal);
			}
		}
		double[][] quadraticClone = new double[Math.max(maxD1, maxD2)][Math.max(maxD1, maxD2)];
		for (int row = 0; row < quadraticClone.length; row++) {
			for (int col = 0; col < quadraticClone[row].length; col++) {
				if (row > values.length - 1 || col > values[row].length - 1)
					quadraticClone[row][col] = maxVal;
				else
					quadraticClone[row][col] = values[row][col];
			}
		}
		return quadraticClone;
	}

	/**
	 * Shrinks and replaces values of the given array according to the given row/column count of the original array. Returned array will be
	 * of size rowCount and contain only values smaller than colCount.
	 * 
	 * @param arrayToReduce
	 * @param rowCount
	 * @param colCount
	 * @return
	 */
	protected static int[] reduceArray(int[] arrayToReduce, int rowCount, int colCount) {
		int[] reducedArray = new int[rowCount];
		for (int i = 0; i < reducedArray.length; i++) {
			reducedArray[i] = arrayToReduce[i] >= colCount ? -1 : arrayToReduce[i];
		}
		return reducedArray;
	}

	protected static class HungarianAlgorithm {
		// Implementation of the Hungarian a.k.a. Munkres-Kuhn algorithm
		// Solves the maximum weighted bipartite matching problem (a.k.a. marriage problem) in O(n)

		// Based on the following source, but strongly refactored
		// http://www.koders.com/java/fid2C93F1FAE2F786A091D64ED84F2A67D2659EC4F2.aspx?s=249#L29

		/*
		 * Munkres-Kuhn (Hungarian) Algorithm Clean Version: 0.11
		 * 
		 * Konstantinos A. Nedas Department of Spatial Information Science & Engineering University of Maine, Orono, ME 04469-5711, USA
		 * kostas@spatial.maine.edu http://www.spatial.maine.edu/~kostas
		 * 
		 * This Java class implements the Hungarian algorithm [a.k.a Munkres' algorithm, a.k.a. Kuhn algorithm, a.k.a. Assignment problem,
		 * a.k.a. Marriage problem, a.k.a. Maximum Weighted Maximum Cardinality Bipartite Matching].
		 * 
		 * Any comments, corrections, or additions would be much appreciated. Credit due to professor Bob Pilgrim for providing an online
		 * copy of the pseudocode for this algorithm (http://216.249.163.93/bob.pilgrim/445/munkres.html)
		 * 
		 * Feel free to redistribute this source code, as long as this header--with the exception of sections in brackets--remains as part
		 * of the file.
		 * 
		 * Requirements: JDK 1.5.0_01 or better.
		 */

		public static int[] findMatching(double[][] costArray) {
			modifyCostArray(costArray);
			int[][] zeroMask = new int[costArray.length][costArray[0].length];
			int[] coveredRows = new int[costArray.length];
			int[] coveredCols = new int[costArray[0].length];
			int[] storedPrimedZero = new int[2];

			step1(costArray);
			step2(costArray, zeroMask, coveredRows, coveredCols);
			while (true) {
				if (step3(costArray, zeroMask, coveredRows, coveredCols))
					break;
				step4(costArray, zeroMask, coveredRows, coveredCols, storedPrimedZero);
				step5(zeroMask, coveredRows, coveredCols, storedPrimedZero);
			}

			return generateAssignments(costArray, zeroMask);
		}

		private static void step1(double[][] costArray) {
			// Decrease elements in every row by minimum value of each row
			double minval;
			for (int col = 0; col < costArray.length; col++) {
				minval = costArray[col][0];
				for (int row = 0; row < costArray[col].length; row++) {
					if (minval > costArray[col][row])
						minval = costArray[col][row];
				}
				for (int row = 0; row < costArray[col].length; row++) {
					costArray[col][row] = costArray[col][row] - minval;
				}
			}
		}

		private static void step2(double[][] costArray, int[][] zeroMask, int[] coveredRows, int[] coveredCols) {
			// Mark uncovered zeros
			for (int i = 0; i < costArray.length; i++) {
				for (int j = 0; j < costArray[i].length; j++) {
					if (costArray[i][j] == 0 && coveredCols[j] == 0 && coveredRows[i] == 0) {
						zeroMask[i][j] = 1;
						coveredCols[j] = 1;
						coveredRows[i] = 1;
					}
				}
			}
			clearCovers(coveredRows, coveredCols);
		}

		private static boolean step3(double[][] costArray, int[][] zeroMask, int[] coveredRows, int[] coveredCols) {
			coverMaskedColumns(zeroMask, coveredCols);
			return allColumnsCovered(zeroMask, coveredCols) ? true : false;
		}

		private static void step4(double[][] costArray, int[][] zeroMask, int[] coveredRows, int[] coveredCols, int[] storedPrimedZero) {
			int[] row_col = null;
			while (true) {
				// Find an uncovered zero in cost
				while (null == (row_col = findUncoveredZero(costArray, coveredRows, coveredCols))) {
					// Loop step6 until one can be found
					step6(costArray, coveredRows, coveredCols);
				}
				// Prime the found uncovered zero.
				zeroMask[row_col[0]][row_col[1]] = 2;

				if (checkForStarsInRows(zeroMask, row_col)) {
					coveredRows[row_col[0]] = 1; // Cover the star's row.
					coveredCols[row_col[1]] = 0; // Uncover its column.
				} else {
					storedPrimedZero[0] = row_col[0]; // Save row of primed zero.
					storedPrimedZero[1] = row_col[1]; // Save column of primed zero.
					return;
				}
			}
		}

		private static void step5(int[][] zeroMask, int[] coveredRows, int[] coveredCols, int[] storedPrimedZero) {
			// Constructs a series of alternating primes and stars

			// Path matrix (stores row and col).
			int[][] path = new int[(zeroMask[0].length * zeroMask.length)][2];
			int pathMatrixRowCtr = 0;
			// Start path with last prime from step 4
			path[pathMatrixRowCtr][0] = storedPrimedZero[0];
			path[pathMatrixRowCtr][1] = storedPrimedZero[1];

			// As long there are primes with stars in its column, expand path
			int rowWithStar = -1;
			do {
				// Get index of a row with a star in the same column as the last node in path
				rowWithStar = getStarInColumn(zeroMask, path[pathMatrixRowCtr][1]);
				if (rowWithStar != -1) {
					pathMatrixRowCtr++;
					path[pathMatrixRowCtr][0] = rowWithStar; // Row of starred zero.
					path[pathMatrixRowCtr][1] = path[pathMatrixRowCtr - 1][1]; // Column of starred zero.

					// Get index of column with a prime in the same row
					int colWithPrime = getPrimeInRow(zeroMask, path[pathMatrixRowCtr][0]);
					pathMatrixRowCtr++;
					path[pathMatrixRowCtr][0] = path[pathMatrixRowCtr - 1][0]; // Row of primed zero.
					path[pathMatrixRowCtr][1] = colWithPrime; // Column of primed zero.
				}
			} while (rowWithStar != -1);

			convertPath(zeroMask, path, pathMatrixRowCtr);
			clearCovers(coveredRows, coveredCols);
			erasePrimes(zeroMask);
		}

		private static void step6(double[][] costArray, int[] coveredRows, int[] coveredCols) {
			double minval = findSmallestUncoveredValue(costArray, coveredRows, coveredCols);
			for (int row = 0; row < coveredRows.length; row++) {
				for (int col = 0; col < coveredCols.length; col++) {
					if (coveredRows[row] == 1)
						costArray[row][col] += minval;
					if (coveredCols[col] == 0)
						costArray[row][col] -= minval;
				}
			}
		}

		private static void modifyCostArray(double[][] costArray) {
			double maxWeight = getLargestValueInArray(costArray);
			for (int i = 0; i < costArray.length; i++) {
				for (int j = 0; j < costArray[i].length; j++) {
					costArray[i][j] = (maxWeight - costArray[i][j]);
				}
			}
		}

		private static double getLargestValueInArray(double[][] array) {
			double largest = 0;
			for (int row = 0; row < array.length; row++) {
				for (int col = 0; col < array[row].length; col++) {
					if (array[row][col] > largest)
						largest = array[row][col];
				}
			}
			return largest;
		}

		private static void clearCovers(int[] coveredRows, int[] coveredCols) {
			for (int i = 0; i < coveredRows.length; i++) {
				coveredRows[i] = 0;
			}
			for (int j = 0; j < coveredCols.length; j++) {
				coveredCols[j] = 0;
			}
		}

		private static void coverMaskedColumns(int[][] zeroMask, int[] coveredCols) {
			for (int row = 0; row < zeroMask.length; row++) {
				for (int col = 0; col < zeroMask[row].length; col++) {
					if (zeroMask[row][col] == 1)
						coveredCols[col] = 1;
				}
			}
		}

		private static boolean allColumnsCovered(int[][] zeroMask, int[] coveredCols) {
			int count = 0;
			for (int j = 0; j < coveredCols.length; j++) {
				count = count + coveredCols[j];
			}
			return count >= zeroMask.length;
		}

		private static boolean checkForStarsInRows(int[][] zeroMask, int[] row_col) {
			boolean starInRow = false;
			for (int col = 0; col < zeroMask[row_col[0]].length; col++) {
				// If there is a star in the same row...
				if (zeroMask[row_col[0]][col] == 1) {
					// remember its column.
					row_col[1] = col;
					starInRow = true;
				}
			}
			return starInRow;
		}

		private static int[] findUncoveredZero(double[][] costArray, int[] coveredRows, int[] coveredCols) {
			for (int row = 0; row < coveredCols.length; row++) {
				for (int col = 0; col < coveredCols.length; col++) {
					if (costArray[row][col] == 0 && coveredRows[row] == 0 && coveredCols[col] == 0)
						return new int[] { row, col };
				}
			}
			return null;
		}

		private static int getStarInColumn(int[][] zeroMask, int col) {
			for (int row = 0; row < zeroMask.length; row++) {
				if (zeroMask[row][col] == 1) {
					return row;
				}
			}
			return -1;
		}

		private static int getPrimeInRow(int[][] zeroMask, int row) {
			for (int col = 0; col < zeroMask[row].length; col++) {
				if (zeroMask[row][col] == 2) {
					return col;
				}
			}
			return -1;
		}

		private static void convertPath(int[][] zeroMask, int[][] path, int rowCount) {
			// Unstar all stars and star the primes of the path
			for (int i = 0; i <= rowCount; i++) {
				if (zeroMask[(path[i][0])][(path[i][1])] == 1)
					zeroMask[(path[i][0])][(path[i][1])] = 0;
				else
					zeroMask[(path[i][0])][(path[i][1])] = 1;
			}
		}

		private static void erasePrimes(int[][] zeroMask) {
			for (int row = 0; row < zeroMask.length; row++) {
				for (int col = 0; col < zeroMask[row].length; col++) {
					if (zeroMask[row][col] == 2)
						zeroMask[row][col] = 0;
				}
			}
		}

		private static double findSmallestUncoveredValue(double[][] costArray, int[] coveredRows, int[] coveredCols) {
			double minval = Double.MAX_VALUE;
			for (int row = 0; row < costArray.length; row++) {
				for (int col = 0; col < costArray[row].length; col++) {
					if (coveredRows[row] == 0 && coveredCols[col] == 0 && (minval > costArray[row][col]))
						minval = costArray[row][col];
				}
			}
			return minval;
		}

		private static int[] generateAssignments(double[][] costArray, int[][] zeroMask) {
			int[] assignment = new int[costArray.length];
			for (int row = 0; row < zeroMask.length; row++) {
				for (int col = 0; col < zeroMask[row].length; col++) {
					if (zeroMask[row][col] == 1)
						assignment[row] = col;
				}
			}
			return assignment;
		}
	}

	@SuppressWarnings("unused")
	private static class Matcher {

		// This implementation does not terminate in some cases!
		// Do not use!

		private static class LineZeroIndices {
			private List<Integer> zeros = new ArrayList<Integer>();
			private int index;

			public LineZeroIndices(int index) {
				this.index = index;
			}

			public int getIndex() {
				return index;
			}

			public void addZeroIndex(int zero) {
				zeros.add(zero);
			}

			public List<Integer> getZeroIndices() {
				return zeros;
			}

			public int getFirstZeroIndex() {
				return zeros.get(0);
			}

			public int getLastZeroIndex() {
				return zeros.get(zeros.size() - 1);
			}

			public int getNumberOfZeros() {
				return zeros.size();
			}

			public void removeZeroWithIndex(int index) {
				for (int i = 0; i < zeros.size(); i++) {
					if (zeros.get(i) == index) {
						zeros.remove(i);
						break;
					}
					if (zeros.get(i) > index)
						break;
				}
			}

			@Override
			public String toString() {
				String val = "[" + index + "|";
				for (Integer zeroIndex : zeros) {
					val += " " + zeroIndex;
				}
				return val + "]";
			}

		}

		private static final Comparator<LineZeroIndices> c = new Comparator<LineZeroIndices>() {
			@Override
			public int compare(LineZeroIndices o1, LineZeroIndices o2) {
				if (o1.getZeroIndices().size() > o2.getZeroIndices().size())
					return 1;
				if (o1.getZeroIndices().size() < o2.getZeroIndices().size())
					return -1;
				return 0;
			}
		};

		private static int[] performHungarianAlgorithm(double[][] costs) {

			reduceRowsByMin(costs);
			reduceColumnsByMin(costs);

			while (true) {

				// // Find rows and columns with only one zero
				// int[] rowZerosCtr = new int[costs.length];
				// int[] rowZeroIndex = new int[costs.length];
				// int[] colZerosCtr = new int[costs.length];
				// int[] colZeroIndex = new int[costs.length];
				// List<int[]> zeros = new ArrayList<int[]>();
				//
				// for (int row = 0; row < costs.length; row++) {
				// for (int col = 0; col < costs.length; col++) {
				// if (costs[row][col] == 0) {
				// rowZerosCtr[row]++;
				// rowZeroIndex[row] = col;
				// colZerosCtr[col]++;
				// colZeroIndex[col] = row;
				// zeros.add(new int[] { row, col });
				// }
				// }
				// }

				List<LineZeroIndices> rowsZeroIndices = new ArrayList<LineZeroIndices>();
				List<LineZeroIndices> colsZeroIndices = new ArrayList<LineZeroIndices>();
				for (int i = 0; i < costs.length; i++) {
					rowsZeroIndices.add(new LineZeroIndices(i));
					colsZeroIndices.add(new LineZeroIndices(i));
				}
				for (int row = 0; row < costs.length; row++) {
					for (int col = 0; col < costs.length; col++) {
						if (costs[row][col] == 0) {
							rowsZeroIndices.get(row).addZeroIndex(col);
							colsZeroIndices.get(col).addZeroIndex(row);
						}
					}
				}

				Collections.sort(rowsZeroIndices, c);
				Collections.sort(colsZeroIndices, c);

				List<int[]> assignedZeros = new ArrayList<int[]>();
				List<Integer> eliminatedRows = new ArrayList<Integer>();
				List<Integer> eliminatedCols = new ArrayList<Integer>();

				// Suche eine Kombination von Nullen derart, dass in jeder Zeile und in jeder Spalte nur eine Null ausgewhlt ist.
				// Steht in einer Zeile oder Spalte nur eine einzige Null, so muss sie natrlich in die Lsung.

				// This does not seem to work properly...

				while (rowsZeroIndices.size() + colsZeroIndices.size() > 0) {
					int minNumberOfZeros = Integer.MAX_VALUE;
					for (LineZeroIndices r : rowsZeroIndices) {
						minNumberOfZeros = Math.min(minNumberOfZeros, r.getNumberOfZeros());
					}
					for (LineZeroIndices c : colsZeroIndices) {
						minNumberOfZeros = Math.min(minNumberOfZeros, c.getNumberOfZeros());
					}

					for (int i = rowsZeroIndices.size() - 1; i >= 0; i--) {
						if (rowsZeroIndices.get(i).getNumberOfZeros() <= minNumberOfZeros) {
							int[] assignedZero = new int[] { rowsZeroIndices.get(i).getIndex(), rowsZeroIndices.get(i).getFirstZeroIndex() };
							if (!eliminatedRows.contains(assignedZero[0]) && !eliminatedCols.contains(assignedZero[1])) {
								assignedZeros.add(assignedZero);
								eliminatedRows.add(assignedZero[0]);
								eliminatedCols.add(assignedZero[1]);
							}
						}
					}

					for (Integer eliminatedRow : eliminatedRows) {
						for (int i = rowsZeroIndices.size() - 1; i >= 0; i--) {
							if (rowsZeroIndices.get(i).getIndex() == eliminatedRow)
								rowsZeroIndices.remove(i);
						}
						for (int i = colsZeroIndices.size() - 1; i >= 0; i--) {
							colsZeroIndices.get(i).removeZeroWithIndex(eliminatedRow);
							if (colsZeroIndices.get(i).getNumberOfZeros() == 0)
								colsZeroIndices.remove(i);
						}
					}
					eliminatedRows.clear();

					for (Integer eliminatedCol : eliminatedCols) {
						for (int i = colsZeroIndices.size() - 1; i >= 0; i--) {
							if (colsZeroIndices.get(i).getIndex() == eliminatedCol)
								colsZeroIndices.remove(i);
						}
						for (int i = rowsZeroIndices.size() - 1; i >= 0; i--) {
							rowsZeroIndices.get(i).removeZeroWithIndex(eliminatedCol);
							if (rowsZeroIndices.get(i).getNumberOfZeros() == 0)
								rowsZeroIndices.remove(i);
						}
					}
					eliminatedCols.clear();

					Collections.sort(rowsZeroIndices, c);
					Collections.sort(colsZeroIndices, c);

					for (int i = colsZeroIndices.size() - 1; i >= 0; i--) {
						if (colsZeroIndices.get(i).getNumberOfZeros() <= minNumberOfZeros) {
							int[] assignedZero = new int[] { colsZeroIndices.get(i).getFirstZeroIndex(), colsZeroIndices.get(i).getIndex() };
							if (!eliminatedRows.contains(assignedZero[0]) && !eliminatedCols.contains(assignedZero[1])) {
								assignedZeros.add(assignedZero);
								eliminatedRows.add(assignedZero[0]);
								eliminatedCols.add(assignedZero[1]);
							}
						}
					}

					for (Integer eliminatedRow : eliminatedRows) {
						for (int i = rowsZeroIndices.size() - 1; i >= 0; i--) {
							if (rowsZeroIndices.get(i).getIndex() == eliminatedRow)
								rowsZeroIndices.remove(i);
						}
						for (int i = colsZeroIndices.size() - 1; i >= 0; i--) {
							colsZeroIndices.get(i).removeZeroWithIndex(eliminatedRow);
							if (colsZeroIndices.get(i).getNumberOfZeros() == 0)
								colsZeroIndices.remove(i);
						}
					}
					eliminatedRows.clear();

					for (Integer eliminatedCol : eliminatedCols) {
						for (int i = colsZeroIndices.size() - 1; i >= 0; i--) {
							if (colsZeroIndices.get(i).getIndex() == eliminatedCol)
								colsZeroIndices.remove(i);
						}
						for (int i = rowsZeroIndices.size() - 1; i >= 0; i--) {
							rowsZeroIndices.get(i).removeZeroWithIndex(eliminatedCol);
							if (rowsZeroIndices.get(i).getNumberOfZeros() == 0)
								rowsZeroIndices.remove(i);
						}
					}
					eliminatedCols.clear();

					Collections.sort(rowsZeroIndices, c);
					Collections.sort(colsZeroIndices, c);
				}

				// while (zeros.size() > 0) {
				// for (int i = 0; i < costs.length; i++) {
				// if (rowZerosCtr[i] == 1 && !eliminatedRows.contains(i) && !eliminatedCols.contains(rowZeroIndex[i])) {
				// int[] assignedZero = new int[] { i, rowZeroIndex[i] };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// }
				// if (colZerosCtr[i] == 1 && !eliminatedRows.contains(colZeroIndex[i]) && !eliminatedCols.contains(i)) {
				// int[] assignedZero = new int[] { colZeroIndex[i], i };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// }
				// }
				// if (zeros.size() == 0)
				// break;
				// else {
				// int[] assignedZero = null;
				// for (int row = 0; row < costs.length; row++) {
				// for (int col = 0; col < costs.length; col++) {
				// if (costs[row][col] == 0 && ( rowZerosCtr[row] > 1 || colZerosCtr[col] > 1) && !eliminatedRows.contains(row) &&
				// !eliminatedCols.contains(col)) {
				// assignedZero = new int[] { row, col };
				// break;
				// }
				// }
				// if (assignedZero != null)
				// break;
				// }
				//
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				//
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				//
				// for (int row = 0; row < costs.length; row++) {
				// for (int col = 0; col < costs.length; col++) {
				//
				// if (costs[row][col] == 0) {
				// if (row == assignedZero[0] || col == assignedZero[1]) {
				// rowZerosCtr[row]--;
				// colZerosCtr[col]--;
				// }
				// }
				// }
				// }
				//
				// // for (int i = 0; i < costs.length; i++) {
				// // if(costs[i][assignedZero[1]] == 0)
				// // rowZerosCtr[i]--;
				// // if(costs[assignedZero[0]][i] == 0)
				// // colZerosCtr[i]--;
				// // }
				// }
				// }

				// while (zeros.size() > 0) {
				// for (int i = 0; i < costs.length; i++) {
				// if (rowZerosCtr[i] == 1 && !eliminatedRows.contains(i) && !eliminatedCols.contains(rowZeroIndex[i])) {
				// int[] assignedZero = new int[] { i, rowZeroIndex[i] };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// }
				// if (colZerosCtr[i] == 1 && !eliminatedRows.contains(colZeroIndex[i]) && !eliminatedCols.contains(i)) {
				// int[] assignedZero = new int[] { colZeroIndex[i], i };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// }
				// }
				// for (int i = 0; i < costs.length; i++) {
				// if (zeros.size() == 0)
				// break;
				//
				// // If for a row and a column, there are two or more zeros
				// // and one cannot be chosen by inspection, choose the cell arbitrarily for assignment.
				// if (rowZerosCtr[i] > 1 && !eliminatedRows.contains(i)) {
				// for (int j = 0; j < costs[0].length; j++) {
				// if (costs[i][j] == 0 && !eliminatedCols.contains(j)) {
				// int[] assignedZero = new int[] { i, j };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// break;
				// }
				// }
				// }
				// if (colZerosCtr[i] > 1 && !eliminatedCols.contains(i)) {
				// for (int j = 0; j < costs[0].length; j++) {
				// if (costs[j][i] == 0 && !eliminatedRows.contains(j)) {
				// int[] assignedZero = new int[] { j, i };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// break;
				// }
				// }
				// }
				// }
				// }

				// while (zeros.size() > 0) {
				// for (int i = 0; i < costs.length; i++) {
				// if (rowZerosCtr[i] == 1 && !eliminatedRows.contains(i) && !eliminatedCols.contains(rowZeroIndex[i])) {
				// int[] assignedZero = new int[] { i, rowZeroIndex[i] };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// }
				// if (colZerosCtr[i] == 1 && !eliminatedRows.contains(colZeroIndex[i]) && !eliminatedCols.contains(i)) {
				// int[] assignedZero = new int[] { colZeroIndex[i], i };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// }
				// }
				// for (int i = 0; i < costs.length; i++) {
				// if (zeros.size() == 0)
				// break;
				//
				// // If for a row and a column, there are two or more zeros
				// // and one cannot be chosen by inspection, choose the cell arbitrarily for assignment.
				// if (rowZerosCtr[i] > 1 && !eliminatedRows.contains(i)) {
				// for (int j = 0; j < costs[0].length; j++) {
				// if (costs[i][j] == 0 && !eliminatedCols.contains(j)) {
				// int[] assignedZero = new int[] { i, j };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// break;
				// }
				// }
				// }
				// if (colZerosCtr[i] > 1 && !eliminatedCols.contains(i)) {
				// for (int j = 0; j < costs[0].length; j++) {
				// if (costs[j][i] == 0 && !eliminatedRows.contains(j)) {
				// int[] assignedZero = new int[] { j, i };
				// assignedZeros.add(assignedZero);
				//
				// for (int zerosIndex = zeros.size() - 1; zerosIndex >= 0; zerosIndex--) {
				// if (zeros.get(zerosIndex)[0] == assignedZero[0] || zeros.get(zerosIndex)[1] == assignedZero[1])
				// zeros.remove(zerosIndex);
				// }
				// eliminatedRows.add(assignedZero[0]);
				// eliminatedCols.add(assignedZero[1]);
				// break;
				// }
				// }
				// }
				// }
				// }

				if (assignedZeros.size() == costs.length) {
					// optimal solution found - done
					int[] returnValue = new int[costs.length];
					for (int[] assignedZero : assignedZeros) {
						returnValue[assignedZero[0]] = assignedZero[1];
					}
					return returnValue;
				}

				List<Integer> markedRows = new ArrayList<Integer>();
				List<Integer> markedCols = new ArrayList<Integer>();

				// Mark all the rows that do not have assignments.
				for (int row = 0; row < costs.length; row++) {
					boolean assignmentFound = false;
					for (int[] assignedZero : assignedZeros) {
						if (assignedZero[0] == row) {
							assignmentFound = true;
							break;
						}
					}
					if (!assignmentFound)
						markedRows.add(row);
				}

				int newMarks = 0;
				do {
					newMarks = 0;

					// Mark all the columns (not already marked) which have zeros in the marked rows
					for (Integer markedRow : markedRows) {
						for (int col = 0; col < costs[0].length; col++) {
							if (costs[markedRow][col] == 0 && !markedCols.contains(col)) {
								markedCols.add(col);
								newMarks++;
							}
						}
					}

					// Mark all the rows (not already marked) that have assignments in marked columns
					for (Integer markedCol : markedCols) {
						for (int[] assignedZero : assignedZeros) {
							if (assignedZero[1] == markedCol && !markedRows.contains(assignedZero[0])) {
								markedRows.add(assignedZero[0]);
								newMarks++;
							}
						}
					}
				} while (newMarks != 0);

				// Draw straight lines through all unmarked rows and marked columns.
				List<Integer> crossedRows = new ArrayList<Integer>();
				for (int row = 0; row < costs.length; row++) {
					if (!markedRows.contains(row))
						crossedRows.add(row);
				}
				List<Integer> crossedCols = new ArrayList<Integer>(markedCols);

				// Get smallest uncovered element from all uncovered elements
				double smallestElement = Double.MAX_VALUE;
				for (int row = 0; row < costs.length; row++) {
					if (!crossedRows.contains(row)) {
						for (int col = 0; col < costs.length; col++) {
							if (!crossedCols.contains(col)) {
								if (smallestElement > costs[row][col])
									smallestElement = costs[row][col];
							}
						}
					}
				}
				for (int row = 0; row < costs.length; row++) {
					for (int col = 0; col < costs.length; col++) {
						// Subtract smallest uncovered element from all uncovered elements
						if (!crossedRows.contains(row) && !crossedCols.contains(col))
							costs[row][col] -= smallestElement;
						// Add it smallest uncovered element to all line intersections
						if (crossedRows.contains(row) && crossedCols.contains(col))
							costs[row][col] += smallestElement;
					}
				}
			}
		}

		private static void reduceRowsByMin(double[][] costs) {
			for (int row = 0; row < costs.length; row++) {
				double rowMin = costs[row][0];
				for (int col = 1; col < costs.length; col++) {
					rowMin = Math.min(rowMin, costs[row][col]);
				}
				for (int col = 0; col < costs.length; col++) {
					costs[row][col] -= rowMin;
				}
			}
		}

		private static void reduceColumnsByMin(double[][] costs) {
			for (int col = 0; col < costs.length; col++) {
				double colMin = costs[0][col];
				for (int row = 1; row < costs.length; row++) {
					colMin = Math.min(colMin, costs[row][col]);
				}
				for (int row = 0; row < costs.length; row++) {
					costs[row][col] -= colMin;
				}
			}
		}

	}
}
