package by.azgaar.storage.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomAlphaNumeric {

	public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String lower = upper.toLowerCase(Locale.ROOT);
	public static final String digits = "0123456789";
	public static final String alphanum = upper + lower + digits;
	private final Random random;
	private final char[] symbols;
	private final char[] buf;

	public String nextString() {
		for (int i = 0; i < buf.length; ++i) {
			buf[i] = symbols[random.nextInt(symbols.length)];
		}

		return new String(buf);
	}

	public RandomAlphaNumeric(final int length, final Random random, final String symbols) {
		if (length < 1) {
			throw new IllegalArgumentException();
		}

		if (symbols.length() < 2) {
			throw new IllegalArgumentException();
		}

		this.random = Objects.requireNonNull(random);
		this.symbols = symbols.toCharArray();
		this.buf = new char[length];
	}

	public RandomAlphaNumeric(final int length, final Random random) {
		this(length, random, alphanum);
	}

	public RandomAlphaNumeric(final int length) {
		this(length, new SecureRandom());
	}

	public RandomAlphaNumeric() {
		this(32);
	}

}