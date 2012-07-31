package ch.scythe.hsr.helper;

public class TextHelper {
	/** Method to join array elements of type string
	 * 
	 * @author Hendrik Will, imwill.com
	 * 
	 * @param inputArray
	 *            Array which contains strings
	 * @param glueString
	 *            String between each array element
	 * @return String containing all array elements separated by glue string */
	public static String implodeArray(String[] inputArray, String glueString) {

		/** Output variable */
		String output = "";

		if (inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(inputArray[0]);

			for (int i = 1; i < inputArray.length; i++) {
				sb.append(glueString);
				sb.append(inputArray[i]);
			}

			output = sb.toString();
		}

		return output;
	}

	/** Will remove some unwanted characters from the string parameter.
	 * 
	 * @return a sanitized version of the string */
	public static String sanitize(String string) {
		return string.replace("\n", "");
	}

}
