/*
 * Copyright 2014 Julien Dramaix.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.gwt.resources.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.resources.client.AutoConversionBundle.ConstantConditional;
import com.google.gwt.resources.client.AutoConversionBundle.ConstantRenaming;
import com.google.gwt.resources.client.AutoConversionBundle.LenientExternal;

public class AutoConversionTest extends GWTTestCase {
  @Override
  public String getModuleName() {
    return "com.google.gwt.resources.AutoConversion";
  }

  public void testConstantRenaming() {
    ConstantRenaming constantRenaming = res().constantRenaming();

    assertEquals(45, constantRenaming.myConstant());
    assertEquals("38px", constantRenaming.my_constant());
    assertEquals(0, constantRenaming.ie6());
    assertEquals(0, constantRenaming.gecko1_8());
  }

  public void testConstantConditional() {
    ConstantConditional constantConditional = res().constantConditional();

    String expectedCss = "." + constantConditional.foo() + "{width:15px;height:10px;color:black}";
    assertEquals(expectedCss, constantConditional.getText());
    assertEquals("black", constantConditional.color());
    assertEquals(15, constantConditional.width());
    assertEquals(10, constantConditional.height());
  }

  public void testLenientExternal() {
    LenientExternal lenientExternal = res().lenientExternal();

    assertNotSame("obfuscated", lenientExternal.obfuscated());
    assertEquals("nonObfuscated", lenientExternal.nonObfuscated());
    assertEquals("nonObfuscated2", lenientExternal.nonObfuscated2());
    assertEquals("nonObfuscated3", lenientExternal.nonObfuscated3());
  }

  private AutoConversionBundle res() {
    return GWT.create(AutoConversionBundle.class);
  }

}
