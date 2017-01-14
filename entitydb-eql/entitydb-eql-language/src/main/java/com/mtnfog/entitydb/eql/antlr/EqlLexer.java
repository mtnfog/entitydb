// Generated from Eql.g4 by ANTLR 4.0

 	package com.mtnfog.entitydb.eql.antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class EqlLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__11=1, T__10=2, T__9=3, T__8=4, T__7=5, T__6=6, T__5=7, T__4=8, T__3=9, 
		T__2=10, T__1=11, T__0=12, METADATA_FIELD=13, NUMERIC_FIELD=14, OPTION_FIELD=15, 
		STRING_FIELD=16, SORT_ORDER_FIELD=17, INTEGERS=18, STRING=19, WS=20;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'<='", "'select * from entities'", "'order by'", "'!='", "'between'", 
		"'and'", "'>='", "';'", "'<'", "'='", "'>'", "'select * from entities where'", 
		"'metadata'", "'confidence'", "OPTION_FIELD", "STRING_FIELD", "SORT_ORDER_FIELD", 
		"INTEGERS", "STRING", "WS"
	};
	public static final String[] ruleNames = {
		"T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", 
		"T__2", "T__1", "T__0", "METADATA_FIELD", "NUMERIC_FIELD", "OPTION_FIELD", 
		"STRING_FIELD", "SORT_ORDER_FIELD", "INTEGERS", "STRING", "WS"
	};


	public EqlLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Eql.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 18: STRING_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: 
		     String s = getText();
		     s = s.substring(1, s.length() - 1); // strip the leading and trailing quotes
		     s = s.replace("\"\"", "\""); // replace all double quotes with single quotes
		     setText(s);
		    break;
		}
	}

	public static final String _serializedATN =
		"\2\4\26\u00ea\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\2\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\n"+
		"\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\5\20\u00a5\n\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\5\21\u00cd\n\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u00d6"+
		"\n\22\3\23\6\23\u00d9\n\23\r\23\16\23\u00da\3\24\3\24\6\24\u00df\n\24"+
		"\r\24\16\24\u00e0\3\24\3\24\3\24\3\25\6\25\u00e7\n\25\r\25\16\25\u00e8"+
		"\2\26\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f"+
		"\1\27\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\2)\26"+
		"\1\3\2\4\7\"\"/<C\\aac|\4\13\13\"\"\u00f4\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\3+\3\2\2\2\5.\3\2\2\2\7E\3\2\2\2\tN\3\2\2\2\13Q\3\2\2\2\r"+
		"Y\3\2\2\2\17]\3\2\2\2\21`\3\2\2\2\23b\3\2\2\2\25d\3\2\2\2\27f\3\2\2\2"+
		"\31h\3\2\2\2\33\u0085\3\2\2\2\35\u008e\3\2\2\2\37\u00a4\3\2\2\2!\u00cc"+
		"\3\2\2\2#\u00d5\3\2\2\2%\u00d8\3\2\2\2\'\u00dc\3\2\2\2)\u00e6\3\2\2\2"+
		"+,\7>\2\2,-\7?\2\2-\4\3\2\2\2./\7u\2\2/\60\7g\2\2\60\61\7n\2\2\61\62\7"+
		"g\2\2\62\63\7e\2\2\63\64\7v\2\2\64\65\7\"\2\2\65\66\7,\2\2\66\67\7\"\2"+
		"\2\678\7h\2\289\7t\2\29:\7q\2\2:;\7o\2\2;<\7\"\2\2<=\7g\2\2=>\7p\2\2>"+
		"?\7v\2\2?@\7k\2\2@A\7v\2\2AB\7k\2\2BC\7g\2\2CD\7u\2\2D\6\3\2\2\2EF\7q"+
		"\2\2FG\7t\2\2GH\7f\2\2HI\7g\2\2IJ\7t\2\2JK\7\"\2\2KL\7d\2\2LM\7{\2\2M"+
		"\b\3\2\2\2NO\7#\2\2OP\7?\2\2P\n\3\2\2\2QR\7d\2\2RS\7g\2\2ST\7v\2\2TU\7"+
		"y\2\2UV\7g\2\2VW\7g\2\2WX\7p\2\2X\f\3\2\2\2YZ\7c\2\2Z[\7p\2\2[\\\7f\2"+
		"\2\\\16\3\2\2\2]^\7@\2\2^_\7?\2\2_\20\3\2\2\2`a\7=\2\2a\22\3\2\2\2bc\7"+
		">\2\2c\24\3\2\2\2de\7?\2\2e\26\3\2\2\2fg\7@\2\2g\30\3\2\2\2hi\7u\2\2i"+
		"j\7g\2\2jk\7n\2\2kl\7g\2\2lm\7e\2\2mn\7v\2\2no\7\"\2\2op\7,\2\2pq\7\""+
		"\2\2qr\7h\2\2rs\7t\2\2st\7q\2\2tu\7o\2\2uv\7\"\2\2vw\7g\2\2wx\7p\2\2x"+
		"y\7v\2\2yz\7k\2\2z{\7v\2\2{|\7k\2\2|}\7g\2\2}~\7u\2\2~\177\7\"\2\2\177"+
		"\u0080\7y\2\2\u0080\u0081\7j\2\2\u0081\u0082\7g\2\2\u0082\u0083\7t\2\2"+
		"\u0083\u0084\7g\2\2\u0084\32\3\2\2\2\u0085\u0086\7o\2\2\u0086\u0087\7"+
		"g\2\2\u0087\u0088\7v\2\2\u0088\u0089\7c\2\2\u0089\u008a\7f\2\2\u008a\u008b"+
		"\7c\2\2\u008b\u008c\7v\2\2\u008c\u008d\7c\2\2\u008d\34\3\2\2\2\u008e\u008f"+
		"\7e\2\2\u008f\u0090\7q\2\2\u0090\u0091\7p\2\2\u0091\u0092\7h\2\2\u0092"+
		"\u0093\7k\2\2\u0093\u0094\7f\2\2\u0094\u0095\7g\2\2\u0095\u0096\7p\2\2"+
		"\u0096\u0097\7e\2\2\u0097\u0098\7g\2\2\u0098\36\3\2\2\2\u0099\u009a\7"+
		"n\2\2\u009a\u009b\7k\2\2\u009b\u009c\7o\2\2\u009c\u009d\7k\2\2\u009d\u00a5"+
		"\7v\2\2\u009e\u009f\7q\2\2\u009f\u00a0\7h\2\2\u00a0\u00a1\7h\2\2\u00a1"+
		"\u00a2\7u\2\2\u00a2\u00a3\7g\2\2\u00a3\u00a5\7v\2\2\u00a4\u0099\3\2\2"+
		"\2\u00a4\u009e\3\2\2\2\u00a5 \3\2\2\2\u00a6\u00a7\7k\2\2\u00a7\u00cd\7"+
		"f\2\2\u00a8\u00a9\7e\2\2\u00a9\u00aa\7q\2\2\u00aa\u00ab\7p\2\2\u00ab\u00ac"+
		"\7v\2\2\u00ac\u00ad\7g\2\2\u00ad\u00ae\7z\2\2\u00ae\u00cd\7v\2\2\u00af"+
		"\u00b0\7f\2\2\u00b0\u00b1\7q\2\2\u00b1\u00b2\7e\2\2\u00b2\u00b3\7w\2\2"+
		"\u00b3\u00b4\7o\2\2\u00b4\u00b5\7g\2\2\u00b5\u00b6\7p\2\2\u00b6\u00b7"+
		"\7v\2\2\u00b7\u00b8\7k\2\2\u00b8\u00cd\7f\2\2\u00b9\u00ba\7v\2\2\u00ba"+
		"\u00bb\7g\2\2\u00bb\u00bc\7z\2\2\u00bc\u00cd\7v\2\2\u00bd\u00be\7v\2\2"+
		"\u00be\u00bf\7{\2\2\u00bf\u00c0\7r\2\2\u00c0\u00cd\7g\2\2\u00c1\u00c2"+
		"\7w\2\2\u00c2\u00c3\7t\2\2\u00c3\u00cd\7k\2\2\u00c4\u00c5\7n\2\2\u00c5"+
		"\u00c6\7c\2\2\u00c6\u00c7\7p\2\2\u00c7\u00c8\7i\2\2\u00c8\u00c9\7w\2\2"+
		"\u00c9\u00ca\7c\2\2\u00ca\u00cb\7i\2\2\u00cb\u00cd\7g\2\2\u00cc\u00a6"+
		"\3\2\2\2\u00cc\u00a8\3\2\2\2\u00cc\u00af\3\2\2\2\u00cc\u00b9\3\2\2\2\u00cc"+
		"\u00bd\3\2\2\2\u00cc\u00c1\3\2\2\2\u00cc\u00c4\3\2\2\2\u00cd\"\3\2\2\2"+
		"\u00ce\u00cf\7c\2\2\u00cf\u00d0\7u\2\2\u00d0\u00d6\7e\2\2\u00d1\u00d2"+
		"\7f\2\2\u00d2\u00d3\7g\2\2\u00d3\u00d4\7u\2\2\u00d4\u00d6\7e\2\2\u00d5"+
		"\u00ce\3\2\2\2\u00d5\u00d1\3\2\2\2\u00d6$\3\2\2\2\u00d7\u00d9\4\62;\2"+
		"\u00d8\u00d7\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00d8\3\2\2\2\u00da\u00db"+
		"\3\2\2\2\u00db&\3\2\2\2\u00dc\u00de\7$\2\2\u00dd\u00df\t\2\2\2\u00de\u00dd"+
		"\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\u00e2\3\2\2\2\u00e2\u00e3\7$\2\2\u00e3\u00e4\b\24\2\2\u00e4(\3\2\2\2"+
		"\u00e5\u00e7\t\3\2\2\u00e6\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00e6"+
		"\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9*\3\2\2\2\n\2\u00a4\u00cc\u00d5\u00da"+
		"\u00de\u00e0\u00e8";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}