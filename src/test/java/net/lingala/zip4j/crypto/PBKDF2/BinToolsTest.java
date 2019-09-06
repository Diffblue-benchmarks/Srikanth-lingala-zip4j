package net.lingala.zip4j.crypto.PBKDF2;

import net.lingala.zip4j.crypto.PBKDF2.BinTools;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

import java.lang.reflect.Array;

public class BinToolsTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void bin2hexInput0OutputNotNull() {

    // Arrange
    final byte[] b = {};

    // Act and Assert result
    Assert.assertEquals("", BinTools.bin2hex(b));
  }

  // Test written by Diffblue Cover.
  @Test
  public void bin2hexInputNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("", BinTools.bin2hex(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hex2binInput0OutputZero() {

    // Act and Assert result
    Assert.assertEquals(0, BinTools.hex2bin('0'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hex2binInputAOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(10, BinTools.hex2bin('A'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hex2binInputcOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(12, BinTools.hex2bin('c'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hex2binInputgOutputIllegalArgumentException() {

    // Act
    thrown.expect(IllegalArgumentException.class);
    BinTools.hex2bin('g');

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void hex2binInputNotNullOutput1() {

    // Arrange
    final String s = "b2";

    // Act
    final byte[] actual = BinTools.hex2bin(s);

    // Assert result
    Assert.assertArrayEquals(new byte[] {(byte)-78}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void hex2binInputNotNullOutputIllegalArgumentException() {

    // Arrange
    final String s = ",";

    // Act
    thrown.expect(IllegalArgumentException.class);
    BinTools.hex2bin(s);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void hex2binInputNotNullOutputIllegalArgumentException2() {

    // Arrange
    final String s =
        "\u0000\u0001\u0004\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";

    // Act
    thrown.expect(IllegalArgumentException.class);
    BinTools.hex2bin(s);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void hex2binInputNotNullOutputIllegalArgumentException3() {

    // Arrange
    final String s =
        "b2\u0000\u0001\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";

    // Act
    thrown.expect(IllegalArgumentException.class);
    BinTools.hex2bin(s);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void hex2binInputNullOutput0() {

    // Arrange
    final String s = null;

    // Act
    final byte[] actual = BinTools.hex2bin(s);

    // Assert result
    Assert.assertArrayEquals(new byte[] {}, actual);
  }
}
