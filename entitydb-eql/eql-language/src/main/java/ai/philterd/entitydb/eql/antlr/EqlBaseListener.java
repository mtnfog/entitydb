/*
 * Copyright 2024 Philterd, LLC
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Generated from Eql.g4 by ANTLR 4.0

 	package ai.philterd.entitydb.eql.antlr;


import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

public class EqlBaseListener implements EqlListener {
	@Override public void enterCondition(EqlParser.ConditionContext ctx) { }
	@Override public void exitCondition(EqlParser.ConditionContext ctx) { }

	@Override public void enterSelect(EqlParser.SelectContext ctx) { }
	@Override public void exitSelect(EqlParser.SelectContext ctx) { }

	@Override public void enterSort(EqlParser.SortContext ctx) { }
	@Override public void exitSort(EqlParser.SortContext ctx) { }

	@Override public void enterCommand(EqlParser.CommandContext ctx) { }
	@Override public void exitCommand(EqlParser.CommandContext ctx) { }

	@Override public void enterOption(EqlParser.OptionContext ctx) { }
	@Override public void exitOption(EqlParser.OptionContext ctx) { }

	@Override public void enterEveryRule(ParserRuleContext ctx) { }
	@Override public void exitEveryRule(ParserRuleContext ctx) { }
	@Override public void visitTerminal(TerminalNode node) { }
	@Override public void visitErrorNode(ErrorNode node) { }
}