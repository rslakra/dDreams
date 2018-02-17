/******************************************************************************
 * Copyright (C) Devamatre Inc. 2009-2018.
 * 
 * This code is licensed to Devamatre under one or more contributor license 
 * agreements. The reproduction, transmission or use of this code or the 
 * snippet is not permitted without prior express written consent of Devamatre. 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the license is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied and the 
 * offenders will be liable for any damages. All rights, including  but not
 * limited to rights created by patent grant or registration of a utility model 
 * or design, are reserved. Technical specifications and features are binding 
 * only insofar as they are specifically and expressly agreed upon in a written 
 * contract.
 * 
 * You may obtain a copy of the License for more details at:
 *      http://www.devamatre.com/licenses/license.txt.
 *      
 * Devamatre reserves the right to modify the technical specifications and or 
 * features without any prior notice.
 *****************************************************************************/
package com.rslakra.jdk8.lambdaexpr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Rohtash Lakra (rohtash.lakra@devamatre.com)
 * @author Rohtash Singh Lakra (rohtash.singh@gmail.com)
 * @created 2018-01-24 07:57:31 AM
 * @version 1.0.0
 * @since 1.0.0
 */
public class LambdaExpression {

	/**
	 * Traditional Approach. Return true if the number is prime otherwise false.
	 * 
	 * @param number
	 * @return
	 */
	private static boolean isPrimeTraditional(int number) {
		if (number < 2) {
			return false;
		}

		for (int i = 2; i < number; i++) {
			if (number % i == 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	private static boolean isPrimeDeclarative(int number) {
		return (number > 1 && IntStream.range(2, number).noneMatch(index -> number % index == 0));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> names = new ArrayList<>();
		names.add("Rohtash");
		names.add("Singh");
		names.add("Lakra");
		names.add("Java");
		System.out.println(names);

		// after sorting
		Comparator<String> lengthComparator = (s1, s2) -> Integer.compare(s1.length(), s2.length());
		System.out.println("After Sorting with lengthComparator.");
		names.sort(lengthComparator);
		System.out.println(names);

		// after sorting
		Comparator<String> stringComparator = (s1, s2) -> s1.compareTo(s2);
		System.out.println("After Sorting with stringComparator.");
		names.sort(stringComparator);
		System.out.println(names);

		int number = 1;
		System.out.println(number + " is isPrimeTraditional:" + LambdaExpression.isPrimeTraditional(number));
		System.out.println(number + " is isPrimeDeclarative:" + LambdaExpression.isPrimeDeclarative(number));
		number = 7;
		System.out.println(number + " is isPrimeTraditional:" + LambdaExpression.isPrimeTraditional(number));
		System.out.println(number + " is isPrimeDeclarative:" + LambdaExpression.isPrimeDeclarative(number));
		number = 9;
		System.out.println(number + " is isPrimeTraditional:" + LambdaExpression.isPrimeTraditional(number));
		System.out.println(number + " is isPrimeDeclarative:" + LambdaExpression.isPrimeDeclarative(number));

	}

}