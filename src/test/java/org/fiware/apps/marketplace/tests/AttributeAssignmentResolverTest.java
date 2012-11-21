package org.fiware.apps.marketplace.tests;

import static org.junit.Assert.*;

import org.fiware.apps.marketplace.helpers.AttributeAssignmentResolver;
import org.junit.Test;

public class AttributeAssignmentResolverTest {

	@Test
	public void getMaximalAttributeAssignmentsTest_1() {
		double[][] values = new double[][] { { 1.0, 0.0, 0.0, }, { 0.0, 1.0, 0.0, }, { 0.0, 0.0, 1.0, } };

		int[] assignments = AttributeAssignmentResolver.getMaximalAttributeAssignments(values);
		assertNotNull(assignments);
		assertEquals(values[0].length, assignments.length);
		assertEquals(0, assignments[0]);
		assertEquals(1, assignments[1]);
		assertEquals(2, assignments[2]);
	}

	@Test
	public void getMaximalAttributeAssignmentsTest_2() {
		double[][] values = new double[][] { { 1.0, 0.0, 0.0, }, { 0.0, 0.0, 1.0, }, { 0.0, 1.0, 0.0, } };

		int[] assignments = AttributeAssignmentResolver.getMaximalAttributeAssignments(values);
		assertNotNull(assignments);
		assertEquals(values[0].length, assignments.length);
		assertEquals(0, assignments[0]);
		assertEquals(2, assignments[1]);
		assertEquals(1, assignments[2]);
	}

	@Test
	public void getMaximalAttributeAssignmentsTest_3() {
		double[][] values = new double[][] { { 1.0, 0.0, 0.0, }, { 0.0, 1.0, 1.0, }, { 0.0, 0.0, 1.0, } };

		int[] assignments = AttributeAssignmentResolver.getMaximalAttributeAssignments(values);
		assertNotNull(assignments);
		assertEquals(values[0].length, assignments.length);
		assertEquals(0, assignments[0]);
		assertEquals(1, assignments[1]);
		assertEquals(2, assignments[2]);
	}

	@Test
	public void getMaximalAttributeAssignmentsTest_4() {
		double[][] values = new double[][] { { 0.0, 0.0, 0.0, 0.0 }, { 0.0, 1.0, 3.0, 3.0 }, { 0.0, 5.0, 5.0, 9.0 },
				{ 0.0, 1.0, 3.0, 7.0 }, };

		int[] assignments = AttributeAssignmentResolver.getMaximalAttributeAssignments(values);
		assertNotNull(assignments);
		assertEquals(values[0].length, assignments.length);
		assertEquals(0, assignments[0]);
		assertEquals(2, assignments[1]);
		assertEquals(1, assignments[2]);
		assertEquals(3, assignments[3]);
	}

	@Test
	public void getMaximalAttributeAssignmentsTest_5() {
		double[][] values = new double[][] { { 1.0, 2.0, 3.0, 4.0, 5.0 }, { 6.0, 7.0, 8.0, 7.0, 2.0 }, { 1.0, 3.0, 4.0, 4.0, 5.0 },
				{ 3.0, 6.0, 2.0, 8.0, 7.0 }, { 4.0, 1.0, 3.0, 5.0, 4.0 } };

		int[] assignments = AttributeAssignmentResolver.getMaximalAttributeAssignments(values);
		assertNotNull(assignments);
		assertEquals(values[0].length, assignments.length);
		assertTrue(values[0][assignments[0]] + values[1][assignments[1]] + values[2][assignments[2]] + values[3][assignments[3]]
				+ values[4][assignments[4]] == 28.0);
	}

	@Test
	public void getMaximalAttributeAssignmentsTest_6() {
		double[][] values = new double[][] { { 10.0, 19.0, 08.0, 15.0, 19.0 }, { 10.0, 18.0, 07.0, 17.0, 19.0 },
				{ 13.0, 16.0, 09.0, 14.0, 19.0 }, { 12.0, 19.0, 08.0, 18.0, 19.0 }, { 14.0, 17.0, 10.0, 19.0, 19.0 } };

		int[] assignments = AttributeAssignmentResolver.getMaximalAttributeAssignments(values);
		assertNotNull(assignments);
		assertEquals(values[0].length, assignments.length);
		assertTrue(values[0][assignments[0]] + values[1][assignments[1]] + values[2][assignments[2]] + values[3][assignments[3]]
				+ values[4][assignments[4]] == 79.0);
	}

	@Test
	public void getMaximalAttributeAssignmentsTest_7() {
		double[][] values = new double[][] { { 10.0, 19.0, 08.0, 15.0 }, { 10.0, 18.0, 07.0, 17.0 }, { 13.0, 16.0, 09.0, 14.0 },
				{ 12.0, 19.0, 08.0, 18.0 }, { 14.0, 17.0, 10.0, 19.0 } };

		int[] assignments = AttributeAssignmentResolver.getMaximalAttributeAssignments(values);
		assertNotNull(assignments);

		boolean foundMinus1 = false;
		double sum = 0;
		for (int i = 0; i < assignments.length; i++) {
			if (assignments[i] == -1) {
				foundMinus1 = true;
			} else {
				sum += values[i][assignments[i]];
			}
		}
		assertTrue(foundMinus1);
		assertTrue(sum == 60.0);
	}

	@Test
	public void getMaximalAttributeAssignmentsTest_8() {
		double[][] values = new double[][] { { 1, 1, 1 }, { 0, 1, 1 }, { 1, 1, 0 } };
		int[] assignments = AttributeAssignmentResolver.getMaximalAttributeAssignments(values);
		assertNotNull(assignments);
		assertTrue(values[0][assignments[0]] + values[1][assignments[1]] + values[2][assignments[2]] == 3);
	}
}
