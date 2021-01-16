package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringUtils {

	public static final String[] EMPTY_ARRAY = {};
	
	private static Pattern NUMBER_PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	public static boolean isBlank(String str) {
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}

	/**
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		return NUMBER_PATTERN.matcher(str).matches();
	}

	/**
	 * 按照指定的分隔符把字符串分解为字符数组，同时截掉每一个元素两端的空白字符，并忽略掉空字符元素；
	 * 
	 * @param str       要被截断的字符串；
	 * @param delimiter 分隔符；
	 * @return
	 */
	public static String[] splitToArray(String str, String delimiter) {
		return splitToArray(str, delimiter, true, true);
	}

	/**
	 * 按照指定的分隔符把字符串分解为字符数组
	 * 
	 * @param str                要被截断的字符串；
	 * @param delimiter          分隔符；
	 * @param trimElement        是否截断元素两端的空白字符；
	 * @param ignoreEmptyElement 是否忽略空字符元素；
	 * @return
	 */
	public static String[] splitToArray(String str, String delimiter, boolean trimElement, boolean ignoreEmptyElement) {
		if (str == null) {
			return EMPTY_ARRAY;
		}
		if (trimElement) {
			str = str.trim();
		}
		if (str.length() == 0) {
			return EMPTY_ARRAY;
		}
		StringTokenizer tokenizer = new StringTokenizer(str, delimiter);
		List<String> tokens = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (trimElement) {
				token = token.trim();
			}
			if ((!ignoreEmptyElement) || token.length() > 0) {
				tokens.add(token);
			}
		}
		return tokens.toArray(new String[tokens.size()]);
	}

	public static String trim(String str) {
		return str == null ? "" : str.trim();
	}
}