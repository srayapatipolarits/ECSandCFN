package com.sp.web.assessment.personality;

import com.sp.web.exception.SPException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;



/**
 * @author daxabraham
 * 
 *         The class to store the range segment values.
 */
public class RangeBean {
  
  private static final Integer[] NO_INTS = new Integer[0];

  private static final Logger LOG = Logger.getLogger(RangeBean.class);
  
  private int[] valueArray; // the values to use for given indexes
  /*
   * if the index is negative factor is the value to add to the index to get to the index in the
   * value array
   */
  private int factor;
  
  /**
   * Constructor for creating the range bean.
   * 
   * @param factor
   *          - factor
   * @param rangeValues
   *          - range values
   */
  public RangeBean(int factor, ArrayList<Integer> rangeValues) {
    this.factor = factor;
    //valueArray = Ints.toArray(rangeValues);
    valueArray = ArrayUtils.toPrimitive(rangeValues.toArray(NO_INTS));
  }
  
  public int[] getValueArray() {
    return valueArray;
  }
  
  public void setValueArray(int[] valueArray) {
    this.valueArray = valueArray;
  }
  
  public int getFactor() {
    return factor;
  }
  
  public void setFactor(int factor) {
    this.factor = factor;
  }
  
  /**
   * @param rangeIndex
   *          - the index in the range value array.
   * @return - the segment value
   */
  public int getSegmentValue(int rangeIndex) {
    try {
      return valueArray[rangeIndex + factor];
    } catch (Exception e) {
      LOG.debug("Exception processing rangeIndex:" + rangeIndex + ": factor:" + factor
          + ": valueArray:" + Arrays.toString(valueArray), e);
      throw new SPException("Exception processing rangeIndex:" + rangeIndex + ": factor:" + factor
          + ": valueArray:" + Arrays.toString(valueArray), e);
    }
  }
  
}
