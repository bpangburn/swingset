package com.nqadmin.swingset.utils;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SSPropertiesTest {
  @Test
  void loadsBundledDefaults() {
    assertNotNull(SSProperties.class.getClassLoader().getResource("swingset.properties"),
        "swingset.properties must be on the runtime classpath");
    Properties properties = SSProperties.getProperties();
    assertFalse(properties.isEmpty());
    assertEquals("600", properties.getProperty("Frame_Height"));
    assertEquals("800", properties.getProperty("Frame_Width"));
    assertEquals("0", properties.getProperty("Level_1_X_Position"));
    assertSame(properties, SSProperties.getProperties());
  }
}
