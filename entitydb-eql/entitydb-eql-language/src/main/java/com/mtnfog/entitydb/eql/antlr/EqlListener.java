// Generated from Eql.g4 by ANTLR 4.0

 	package com.mtnfog.entitydb.eql.antlr;

import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface EqlListener extends ParseTreeListener {
	void enterCondition(EqlParser.ConditionContext ctx);
	void exitCondition(EqlParser.ConditionContext ctx);

	void enterSelect(EqlParser.SelectContext ctx);
	void exitSelect(EqlParser.SelectContext ctx);

	void enterSort(EqlParser.SortContext ctx);
	void exitSort(EqlParser.SortContext ctx);

	void enterCommand(EqlParser.CommandContext ctx);
	void exitCommand(EqlParser.CommandContext ctx);

	void enterOption(EqlParser.OptionContext ctx);
	void exitOption(EqlParser.OptionContext ctx);
}