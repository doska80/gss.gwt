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

import com.google.common.collect.Lists;
import com.google.common.css.compiler.ast.CssAtRuleNode.Type;
import com.google.common.css.compiler.ast.CssBooleanExpressionNode;
import com.google.common.css.compiler.ast.CssCompilerPass;
import com.google.common.css.compiler.ast.CssConditionalBlockNode;
import com.google.common.css.compiler.ast.CssConditionalRuleNode;
import com.google.common.css.compiler.ast.CssValueNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.MutatingVisitController;
import com.google.gwt.resources.gss.ast.CssJavaExpressionNode;
import com.google.gwt.resources.gss.ast.CssRuntimeConditionalRuleNode;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateRuntimeConditionalNodes extends DefaultTreeVisitor implements CssCompilerPass {
  private static final Pattern EVAL_FUNCTION = Pattern.compile("^eval\\([\"']([^\"']*)[\"']\\)$");

  private final MutatingVisitController visitController;

  public CreateRuntimeConditionalNodes(MutatingVisitController visitController) {
    this.visitController = visitController;
  }

  @Override
  public boolean enterConditionalBlock(CssConditionalBlockNode block) {
    // we have to visit all the CssConditionalRuleNode when we visit the CssConditionalBlockNode
    // parent node because we are going to replace CssConditionalRuleNode by another node and
    // unfortunately the visitController doesn't support to replace a CssConditionalRuleNode and
    // we have to do it manually. That implies that the new nodes won't be visited by the
    // visitor if we do that during the visit of the CssConditionalRuleNodes.

    // make a copy in order to avoid ConcurrentModificationException
    List<CssConditionalRuleNode> children = Lists.newArrayList(block.getChildren());
    for (CssConditionalRuleNode ruleNode : children) {
      manuallyVisitConditionalRule(ruleNode, block);
    }
    return true;
  }

  private void manuallyVisitConditionalRule(CssConditionalRuleNode node,
      CssConditionalBlockNode parent) {

    if (node.getType() != Type.ELSE) {
      CssBooleanExpressionNode nodeCondition = node.getCondition();
      String condition = extractRuntimeCondition(nodeCondition);

      if (condition != null) {
        CssJavaExpressionNode newNode = new CssJavaExpressionNode(condition,
            nodeCondition.getSourceCodeLocation());

        CssRuntimeConditionalRuleNode newRuleNode = new CssRuntimeConditionalRuleNode(node,
            newNode);

        // unfortunately visitController.replaceCurrentBlockChildWith doesn't work with
        // CssConditionnalRuleNode
        int index = parent.getChildren().indexOf(node);
        parent.replaceChildAt(index, Lists.newArrayList(newRuleNode));
      }
    }
  }

  private String extractRuntimeCondition(CssValueNode node) {
    Matcher m = EVAL_FUNCTION.matcher(node.getValue());
    return m.matches() ? m.group(1) : null;
  }

  @Override
  public void runPass() {
    visitController.startVisit(this);
  }
}
