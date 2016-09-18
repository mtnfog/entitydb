// Generated from Eql.g4 by ANTLR 4.0

 	package com.mtnfog.eql.antlr;


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