// Generated from Eql.g4 by ANTLR 4.0

 	package com.mtnfog.eql.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class EqlParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__11=1, T__10=2, T__9=3, T__8=4, T__7=5, T__6=6, T__5=7, T__4=8, T__3=9, 
		T__2=10, T__1=11, T__0=12, ENRICHMENT_FIELD=13, NUMERIC_FIELD=14, OPTION_FIELD=15, 
		STRING_FIELD=16, SORT_ORDER_FIELD=17, INTEGERS=18, STRING=19, WS=20;
	public static final String[] tokenNames = {
		"<INVALID>", "'<='", "'select * from entities'", "'order by'", "'!='", 
		"'between'", "'and'", "'>='", "';'", "'<'", "'='", "'>'", "'select * from entities where'", 
		"'enrichment'", "'confidence'", "OPTION_FIELD", "STRING_FIELD", "SORT_ORDER_FIELD", 
		"INTEGERS", "STRING", "WS"
	};
	public static final int
		RULE_command = 0, RULE_select = 1, RULE_condition = 2, RULE_option = 3, 
		RULE_sort = 4;
	public static final String[] ruleNames = {
		"command", "select", "condition", "option", "sort"
	};

	@Override
	public String getGrammarFileName() { return "Eql.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public EqlParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class CommandContext extends ParserRuleContext {
		public SelectContext select() {
			return getRuleContext(SelectContext.class,0);
		}
		public SortContext sort() {
			return getRuleContext(SortContext.class,0);
		}
		public CommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).enterCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).exitCommand(this);
		}
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_command);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10); select();
			setState(12);
			_la = _input.LA(1);
			if (_la==WS) {
				{
				setState(11); sort();
				}
			}

			setState(15);
			_la = _input.LA(1);
			if (_la==8) {
				{
				setState(14); match(8);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectContext extends ParserRuleContext {
		public ConditionContext condition(int i) {
			return getRuleContext(ConditionContext.class,i);
		}
		public List<ConditionContext> condition() {
			return getRuleContexts(ConditionContext.class);
		}
		public OptionContext option(int i) {
			return getRuleContext(OptionContext.class,i);
		}
		public TerminalNode WS(int i) {
			return getToken(EqlParser.WS, i);
		}
		public List<TerminalNode> WS() { return getTokens(EqlParser.WS); }
		public List<OptionContext> option() {
			return getRuleContexts(OptionContext.class);
		}
		public SelectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).enterSelect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).exitSelect(this);
		}
	}

	public final SelectContext select() throws RecognitionException {
		SelectContext _localctx = new SelectContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_select);
		try {
			int _alt;
			setState(51);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(17); match(2);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(18); match(2);
				setState(22);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
				while ( _alt!=2 && _alt!=-1 ) {
					if ( _alt==1 ) {
						{
						{
						setState(19); option();
						}
						} 
					}
					setState(24);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
				}
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(25); match(12);
				setState(26); condition();
				setState(32);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
				while ( _alt!=2 && _alt!=-1 ) {
					if ( _alt==1 ) {
						{
						{
						setState(27); match(WS);
						setState(28); match(6);
						setState(29); condition();
						}
						} 
					}
					setState(34);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
				}
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(35); match(12);
				setState(36); condition();
				setState(42);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				while ( _alt!=2 && _alt!=-1 ) {
					if ( _alt==1 ) {
						{
						{
						setState(37); match(WS);
						setState(38); match(6);
						setState(39); condition();
						}
						} 
					}
					setState(44);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				}
				setState(48);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
				while ( _alt!=2 && _alt!=-1 ) {
					if ( _alt==1 ) {
						{
						{
						setState(45); option();
						}
						} 
					}
					setState(50);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConditionContext extends ParserRuleContext {
		public Token operator;
		public Token value;
		public Token value1;
		public Token value2;
		public TerminalNode STRING_FIELD() { return getToken(EqlParser.STRING_FIELD, 0); }
		public TerminalNode NUMERIC_FIELD() { return getToken(EqlParser.NUMERIC_FIELD, 0); }
		public TerminalNode ENRICHMENT_FIELD() { return getToken(EqlParser.ENRICHMENT_FIELD, 0); }
		public List<TerminalNode> INTEGERS() { return getTokens(EqlParser.INTEGERS); }
		public TerminalNode INTEGERS(int i) {
			return getToken(EqlParser.INTEGERS, i);
		}
		public List<TerminalNode> STRING() { return getTokens(EqlParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(EqlParser.STRING, i);
		}
		public TerminalNode WS(int i) {
			return getToken(EqlParser.WS, i);
		}
		public List<TerminalNode> WS() { return getTokens(EqlParser.WS); }
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).exitCondition(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_condition);
		int _la;
		try {
			setState(95);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(53); match(WS);
				setState(54); match(NUMERIC_FIELD);
				setState(56);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(55); match(WS);
					}
				}

				setState(58);
				((ConditionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 7) | (1L << 9) | (1L << 10) | (1L << 11))) != 0)) ) {
					((ConditionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				consume();
				setState(60);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(59); match(WS);
					}
				}

				setState(62); ((ConditionContext)_localctx).value = match(INTEGERS);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(63); match(WS);
				setState(64); match(NUMERIC_FIELD);
				setState(65); match(WS);
				setState(66); match(5);
				setState(67); match(WS);
				setState(68); ((ConditionContext)_localctx).value1 = match(INTEGERS);
				setState(69); match(WS);
				setState(70); match(6);
				setState(71); match(WS);
				setState(72); ((ConditionContext)_localctx).value2 = match(INTEGERS);
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(73); match(WS);
				setState(74); match(STRING_FIELD);
				setState(76);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(75); match(WS);
					}
				}

				setState(78);
				((ConditionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==4 || _la==10) ) {
					((ConditionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				consume();
				setState(80);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(79); match(WS);
					}
				}

				setState(82); ((ConditionContext)_localctx).value = match(STRING);
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(83); match(WS);
				setState(84); match(ENRICHMENT_FIELD);
				setState(85); match(WS);
				setState(86); ((ConditionContext)_localctx).value1 = match(STRING);
				setState(88);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(87); match(WS);
					}
				}

				setState(90); match(10);
				setState(92);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(91); match(WS);
					}
				}

				setState(94); ((ConditionContext)_localctx).value2 = match(STRING);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OptionContext extends ParserRuleContext {
		public Token value;
		public TerminalNode OPTION_FIELD() { return getToken(EqlParser.OPTION_FIELD, 0); }
		public TerminalNode INTEGERS() { return getToken(EqlParser.INTEGERS, 0); }
		public TerminalNode WS(int i) {
			return getToken(EqlParser.WS, i);
		}
		public List<TerminalNode> WS() { return getTokens(EqlParser.WS); }
		public OptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_option; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).enterOption(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).exitOption(this);
		}
	}

	public final OptionContext option() throws RecognitionException {
		OptionContext _localctx = new OptionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_option);
		try {
			setState(105);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(97); match(WS);
				setState(98); match(OPTION_FIELD);
				setState(99); match(WS);
				setState(100); ((OptionContext)_localctx).value = match(INTEGERS);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(101); match(WS);
				setState(102); match(OPTION_FIELD);
				setState(103); match(WS);
				setState(104); ((OptionContext)_localctx).value = match(INTEGERS);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SortContext extends ParserRuleContext {
		public TerminalNode STRING_FIELD() { return getToken(EqlParser.STRING_FIELD, 0); }
		public TerminalNode NUMERIC_FIELD() { return getToken(EqlParser.NUMERIC_FIELD, 0); }
		public TerminalNode WS(int i) {
			return getToken(EqlParser.WS, i);
		}
		public List<TerminalNode> WS() { return getTokens(EqlParser.WS); }
		public TerminalNode SORT_ORDER_FIELD() { return getToken(EqlParser.SORT_ORDER_FIELD, 0); }
		public SortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sort; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).enterSort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EqlListener ) ((EqlListener)listener).exitSort(this);
		}
	}

	public final SortContext sort() throws RecognitionException {
		SortContext _localctx = new SortContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_sort);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107); match(WS);
			setState(108); match(3);
			setState(109); match(WS);
			setState(110);
			_la = _input.LA(1);
			if ( !(_la==NUMERIC_FIELD || _la==STRING_FIELD) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(113);
			_la = _input.LA(1);
			if (_la==WS) {
				{
				setState(111); match(WS);
				setState(112); match(SORT_ORDER_FIELD);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\2\3\26v\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\5\2\17\n\2\3"+
		"\2\5\2\22\n\2\3\3\3\3\3\3\7\3\27\n\3\f\3\16\3\32\13\3\3\3\3\3\3\3\3\3"+
		"\3\3\7\3!\n\3\f\3\16\3$\13\3\3\3\3\3\3\3\3\3\3\3\7\3+\n\3\f\3\16\3.\13"+
		"\3\3\3\7\3\61\n\3\f\3\16\3\64\13\3\5\3\66\n\3\3\4\3\4\3\4\5\4;\n\4\3\4"+
		"\3\4\5\4?\n\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\5\4O\n\4\3\4\3\4\5\4S\n\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4[\n\4\3\4\3\4\5"+
		"\4_\n\4\3\4\5\4b\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5l\n\5\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\5\6t\n\6\3\6\2\7\2\4\6\b\n\2\5\5\3\3\t\t\13\r\4\6\6\f"+
		"\f\4\20\20\22\22\u0084\2\f\3\2\2\2\4\65\3\2\2\2\6a\3\2\2\2\bk\3\2\2\2"+
		"\nm\3\2\2\2\f\16\5\4\3\2\r\17\5\n\6\2\16\r\3\2\2\2\16\17\3\2\2\2\17\21"+
		"\3\2\2\2\20\22\7\n\2\2\21\20\3\2\2\2\21\22\3\2\2\2\22\3\3\2\2\2\23\66"+
		"\7\4\2\2\24\30\7\4\2\2\25\27\5\b\5\2\26\25\3\2\2\2\27\32\3\2\2\2\30\26"+
		"\3\2\2\2\30\31\3\2\2\2\31\66\3\2\2\2\32\30\3\2\2\2\33\34\7\16\2\2\34\""+
		"\5\6\4\2\35\36\7\26\2\2\36\37\7\b\2\2\37!\5\6\4\2 \35\3\2\2\2!$\3\2\2"+
		"\2\" \3\2\2\2\"#\3\2\2\2#\66\3\2\2\2$\"\3\2\2\2%&\7\16\2\2&,\5\6\4\2\'"+
		"(\7\26\2\2()\7\b\2\2)+\5\6\4\2*\'\3\2\2\2+.\3\2\2\2,*\3\2\2\2,-\3\2\2"+
		"\2-\62\3\2\2\2.,\3\2\2\2/\61\5\b\5\2\60/\3\2\2\2\61\64\3\2\2\2\62\60\3"+
		"\2\2\2\62\63\3\2\2\2\63\66\3\2\2\2\64\62\3\2\2\2\65\23\3\2\2\2\65\24\3"+
		"\2\2\2\65\33\3\2\2\2\65%\3\2\2\2\66\5\3\2\2\2\678\7\26\2\28:\7\20\2\2"+
		"9;\7\26\2\2:9\3\2\2\2:;\3\2\2\2;<\3\2\2\2<>\t\2\2\2=?\7\26\2\2>=\3\2\2"+
		"\2>?\3\2\2\2?@\3\2\2\2@b\7\24\2\2AB\7\26\2\2BC\7\20\2\2CD\7\26\2\2DE\7"+
		"\7\2\2EF\7\26\2\2FG\7\24\2\2GH\7\26\2\2HI\7\b\2\2IJ\7\26\2\2Jb\7\24\2"+
		"\2KL\7\26\2\2LN\7\22\2\2MO\7\26\2\2NM\3\2\2\2NO\3\2\2\2OP\3\2\2\2PR\t"+
		"\3\2\2QS\7\26\2\2RQ\3\2\2\2RS\3\2\2\2ST\3\2\2\2Tb\7\25\2\2UV\7\26\2\2"+
		"VW\7\17\2\2WX\7\26\2\2XZ\7\25\2\2Y[\7\26\2\2ZY\3\2\2\2Z[\3\2\2\2[\\\3"+
		"\2\2\2\\^\7\f\2\2]_\7\26\2\2^]\3\2\2\2^_\3\2\2\2_`\3\2\2\2`b\7\25\2\2"+
		"a\67\3\2\2\2aA\3\2\2\2aK\3\2\2\2aU\3\2\2\2b\7\3\2\2\2cd\7\26\2\2de\7\21"+
		"\2\2ef\7\26\2\2fl\7\24\2\2gh\7\26\2\2hi\7\21\2\2ij\7\26\2\2jl\7\24\2\2"+
		"kc\3\2\2\2kg\3\2\2\2l\t\3\2\2\2mn\7\26\2\2no\7\5\2\2op\7\26\2\2ps\t\4"+
		"\2\2qr\7\26\2\2rt\7\23\2\2sq\3\2\2\2st\3\2\2\2t\13\3\2\2\2\22\16\21\30"+
		"\",\62\65:>NRZ^aks";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}