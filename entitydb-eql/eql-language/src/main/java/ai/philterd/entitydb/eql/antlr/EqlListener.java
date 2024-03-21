// Generated from Eql.g4 by ANTLR 4.13.1

 	package ai.philterd.entitydb.eql.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link EqlParser}.
 */
public interface EqlListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link EqlParser#command}.
	 * @param ctx the parse tree
	 */
	void enterCommand(EqlParser.CommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link EqlParser#command}.
	 * @param ctx the parse tree
	 */
	void exitCommand(EqlParser.CommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link EqlParser#select}.
	 * @param ctx the parse tree
	 */
	void enterSelect(EqlParser.SelectContext ctx);
	/**
	 * Exit a parse tree produced by {@link EqlParser#select}.
	 * @param ctx the parse tree
	 */
	void exitSelect(EqlParser.SelectContext ctx);
	/**
	 * Enter a parse tree produced by {@link EqlParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(EqlParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EqlParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(EqlParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EqlParser#option}.
	 * @param ctx the parse tree
	 */
	void enterOption(EqlParser.OptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EqlParser#option}.
	 * @param ctx the parse tree
	 */
	void exitOption(EqlParser.OptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EqlParser#sort}.
	 * @param ctx the parse tree
	 */
	void enterSort(EqlParser.SortContext ctx);
	/**
	 * Exit a parse tree produced by {@link EqlParser#sort}.
	 * @param ctx the parse tree
	 */
	void exitSort(EqlParser.SortContext ctx);
}