package org.fiware.apps.marketplace.model.validators;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its contributors
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import static org.assertj.core.api.Assertions.*;

import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Review;
import org.junit.Test;

public class ReviewValidatorTest {

	private ReviewValidator reviewValidator = new ReviewValidator();
	
	private void testScore(int score) throws ValidationException {
		Review review = new Review();
		review.setScore(score);		
		reviewValidator.validateReview(review);
	}

	private void testInvalidScore(int score) {
		try {
			testScore(score);
		} catch (ValidationException ex) {
			assertThat(ex.getFieldName()).isEqualTo("score");
			assertThat(ex.getMessage()).isEqualTo("Score should be an integer between 1 and 5.");
		}
	}

	@Test
	public void testInvalidScore1() {
		testInvalidScore(0);
	}

	@Test
	public void testInvalidScore2() {
		testInvalidScore(6);
	}

	@Test
	public void testValidScore1() throws ValidationException {
		testScore(1);
	}

	@Test
	public void testValidScore2() throws ValidationException {
		testScore(5);
	}

	@Test
	public void testValidScore3() throws ValidationException {
		testScore(3);
	}

	@Test
	public void testInvalidComment() throws ValidationException {
		
		int maxChars = 1000;
		
		try {
			
			String comment = "";
			for (int i = 0; i < maxChars + 1; i++) {
				comment += "a";
			}
			
			Review review = new Review();
			review.setScore(1);
			review.setComment(comment);

			reviewValidator.validateReview(review);
			failBecauseExceptionWasNotThrown(ValidationException.class);
		} catch (ValidationException ex) {
			assertThat(ex.getFieldName()).isEqualTo("comment");
			assertThat(ex.getMessage()).isEqualTo("This field must not exceed " + maxChars + " chars.");
		}
	}
}
