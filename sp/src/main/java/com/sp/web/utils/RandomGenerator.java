package com.sp.web.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Dax Abraham
 *
 *         This is the class to help generate random numbers and codes.
 */
public class RandomGenerator {
  
  private static final char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789".toCharArray();
  private static Random random = new Random();
  
  /**
   * Get a random string for the given length. Omits 0 and O.
   * 
   * @param length
   *          - length of random string
   * @return
   *      the random string
   */
  public static String randomString(int length) {
    return randomString(CHARSET_AZ_09, length);
  }
  
  /**
   * Get a random string for the given length. Uses the characters from the passed array.
   * 
   * @param characterSet
   *            - the characters to use
   * @param length
   *            - Length of random code
   * @return
   *      the random string
   */
  public static String randomString(char[] characterSet, int length) {
    Random random = new SecureRandom();
    char[] result = new char[length];
    for (int i = 0; i < result.length; i++) {
      // picks a random index out of character set > random character
      int randomCharIndex = random.nextInt(characterSet.length);
      result[i] = characterSet[randomCharIndex];
    }
    return new String(result);
  }
  
  /**
   * getRandomInteger return random 1 or 0 int varaibles.
   * 
   * @return randomely 1 or zero.
   */
  public static int getRandomInteger() {
    return random.nextBoolean() ? 1 : 0;
  }

  /**
   * Gets the next random number between 0(inclusive) to size(exclusive). 
   * 
   * @param size
   *          - bound
   * @return
   *    the random int
   */
  public static int getNextInt(int size) {
    return random.nextInt(size);
  }
}
