package se.tpr.pillerkollen.medicines.add;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class DosageHelper {

	/**
	 * Calculates how to divide the total dosage into the given pills dosages.
	 * The result is stored in numberOfParts.
	 * 
	 * With the parts 10mg, 5mg, 1mg, and the totalDosage of 27mg, 
	 * the result is to take the pills 2x10mg, 1x5mg, 2x1mg
	 *  
	 * @param totalDosage The total dosage of the medicine to take
	 * @param parts The different dosages the medicine is available in
	 * @param numberOfParts The resulting map containing how many pills of a certain dosage to take. [(10mg, 2), (5mg, 1), (1mg, 2)]; 
	 */
	public static void calculateNumberOfParts(BigDecimal totalDosage, List<BigDecimal> parts, Map<BigDecimal, Integer> numberOfParts) {
		if (parts.isEmpty() || totalDosage.signum() <= 0) {
			return;
		}

		BigDecimal part = parts.get(0);

		while (totalDosage.subtract(part).signum() >= 0) {
			Integer number = numberOfParts.get(part) == null ? 0 : numberOfParts.get(part);
			numberOfParts.put(part, number+1);
			totalDosage = totalDosage.subtract(part);
		}

		List<BigDecimal> subList = parts.subList(1, parts.size());

		// If 0.3mg is left and the lowest dosage is 0.5, 0.3 will be returned.
		if (hasNonDividableRest(totalDosage, subList)) {
			BigDecimal formatNumber = totalDosage.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
			numberOfParts.put(formatNumber, 1);
		}
		calculateNumberOfParts(totalDosage, subList, numberOfParts);
	}


	private static boolean hasNonDividableRest(BigDecimal totalDosage, List<BigDecimal> subList) {
		return subList.isEmpty() && totalDosage.signum() == 1;
	}
}
