package com.sp.web.utils;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.utils.og.MetaElement;
import com.sp.web.utils.og.OpenGraph;

import org.junit.Test;

public class OpenGraphTest extends SPTestBase {

  @Test
  public void test() {
    try {
      OpenGraph og = new OpenGraph("https://www.youtube.com/watch?v=x94QcfbTngc", false);
      for (MetaElement elem : og.getProperties()) {
        log.debug(elem.getProperty() + ":" + elem.getContent());
      }
      
      OpenGraph og2 = new OpenGraph("http://www.pepperfry.com/three-door-wardrobe-in-wenge-finish-by-mintwud-1193831.html?type=clip&pos=2:1", true);
      final MetaElement[] properties = og2.getProperties();
      assertThat(properties.length, is(greaterThan(0)));
      for (MetaElement elem : properties) {
        log.debug(elem.getProperty() + ":" + elem.getContent());
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
}
