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

package com.google.gwt.resources.gss;

import com.google.common.css.compiler.ast.CssCompilerPass;
import com.google.common.css.compiler.ast.CssConditionalBlockNode;
import com.google.common.css.compiler.ast.CssConditionalRuleNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.VisitController;
import com.google.gwt.resources.gss.ast.CssRuntimeConditionalRuleNode;

import java.util.HashSet;
import java.util.Set;

public class RuntimeConditionalNodeCollector extends DefaultTreeVisitor implements
    CssCompilerPass {

  private final VisitController visitController;

  private Set<CssConditionalBlockNode> runtimeConditionalNodes;

  public RuntimeConditionalNodeCollector(VisitController visitController) {
    this.visitController = visitController;
  }

  @Override
  public boolean enterConditionalBlock(CssConditionalBlockNode block) {
    for (CssConditionalRuleNode currentConditional : block.childIterable()) {
      if (currentConditional instanceof CssRuntimeConditionalRuleNode) {
        runtimeConditionalNodes.add(block);
        return true;
      }
    }

    return true;
  }


  @Override
  public void runPass() {
    runtimeConditionalNodes = new HashSet<CssConditionalBlockNode>();

    visitController.startVisit(this);
  }

  public Set<CssConditionalBlockNode> getRuntimeConditionalNodes() {
    return runtimeConditionalNodes;
  }
}
