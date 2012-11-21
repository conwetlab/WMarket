package org.fiware.apps.marketplace.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mysql.jdbc.StringUtils;

/**
 * [Under development] Class to handle the unit issue for quantitative attribute comparison.
 * 
 * @author D058352
 * 
 */
public abstract class AttributeUnitFactorResolver {

	// TODO Optimize and complete...

	private static HashMap<String, Double> unitFactorMap;
	private static HashMap<String, String> unitTextMap;
	private static List<List<String>> compatibleUnitLists;

	/**
	 * Returns true if the given two units are comparable (e.g. kilograms and grams) or false if not (e.g. kilograms and wet ducks).
	 * @param sourceUnit String representation of unit according to UN/CEFACT Common Codes for Units.
	 * @param targetUnit String representation of unit according to UN/CEFACT Common Codes for Units.
	 * @return
	 */
	public static boolean unitsComparable(String sourceUnit, String targetUnit) {
		if (compatibleUnitLists == null)
			initCompatibleUnitList();

		if (sourceUnit.equals(targetUnit))
			return true;

		for (List<String> compatibleUnitList : compatibleUnitLists) {
			if (compatibleUnitList.contains(sourceUnit)) {
				for (String compatibleUnit : compatibleUnitList) {
					if (compatibleUnit.equals(targetUnit))
						return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns a factor to multiply a value with to be comparable with another unit.
	 * @param unit String representation of unit according to UN/CEFACT Common Codes for Units.
	 * @return Returns factor or null if given unit is null or empty.
	 */
	public static Double resolveNormalizationFactor(String unit) {
		if (StringUtils.isNullOrEmpty(unit))
			return null;

		if (unitFactorMap == null)
			initUnitFactorMap();

		if (!unitFactorMap.containsKey(unit)) {
			System.out.println(AttributeUnitFactorResolver.class.getName() + " - Unit-Factor-Map does not contain unit: " + unit);
			return null;
		}
		return unitFactorMap.get(unit);
	}
	
	/**
	 * Returns the plaintext representation of the UN/CEFACT Common Code unit
	 * @param unit
	 * @return Returns null if unit is null or empty. Returns code when unit unknown.
	 */
	public static String getPlaintext(String unit) {
		if (StringUtils.isNullOrEmpty(unit))
			return null;

		if (unitTextMap == null)
			initUnitTextMap();
		
		if (!unitTextMap.containsKey(unit))
			return unit;
		
		return unitTextMap.get(unit);
	}
	
	private static synchronized void initCompatibleUnitList() {

		// TODO Resolve from an XML, DB or similar...

		compatibleUnitLists = new ArrayList<List<String>>();
		compatibleUnitLists.add(new ArrayList<String>());
		compatibleUnitLists.get(0).add("C62");
		compatibleUnitLists.add(new ArrayList<String>());
		compatibleUnitLists.get(1).add("A86");
		compatibleUnitLists.add(new ArrayList<String>());
		compatibleUnitLists.get(2).add("E34");
		compatibleUnitLists.get(2).add("E35");
		compatibleUnitLists.get(2).add("4L");
	}

	private static synchronized void initUnitFactorMap() {

		// TODO Resolve from an XML, DB or similar...

		unitFactorMap = new HashMap<String, Double>();
		unitFactorMap.put("C62", 1.0);
		unitFactorMap.put("Mbit/s", 1.0);
		unitFactorMap.put("E34", 1.0 / (Math.pow(10, 9))); // Gigabyte
		unitFactorMap.put("E35", 1.0 / (Math.pow(10, 12))); // Terabyte
		unitFactorMap.put("4L", 1.0 / (Math.pow(10, 6))); // Megabyte
		unitFactorMap.put("A86", 1.0 / (Math.pow(10, 9))); // Gigaherz
	}
	
	private static synchronized void initUnitTextMap() {
		
		// TODO Resolve from an XML, DB or similar...

		unitTextMap = new HashMap<String, String>();
		unitTextMap.put("C62", "");
		unitTextMap.put("E34", "GB"); 
		unitTextMap.put("E35", "TB");
		unitTextMap.put("4L", "MB"); 
		unitTextMap.put("A86", "GHz");
	}
}
