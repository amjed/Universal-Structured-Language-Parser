/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.common;

import java.text.ParseException;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 *  The primitive parser is a helper class used to parse primitive types from strings.
 *
 *  Please note that as of late, only parsing primitives are supported.
 *  Additional parsable types can be added by using the registerParser method
 *
 * @author Xevia
 */
public class PrimitiveParser {

    private static PrimitiveParser instance = null;

    public static PrimitiveParser getInstance() {
	if (instance==null) {
	    synchronized (PrimitiveParser.class) {
		if (instance==null) {
		    instance = new PrimitiveParser();
		}
	    }
	}
	return instance;
    }

    public interface Parser<ReturnType> {
	public Class[] getParsedClasses();
	public ReturnType parse(String value) throws ParseException ;
    }

    private class StringParser implements Parser<String> {
	public String parse( String value ) {
	    return value;
	}

	public Class[] getParsedClasses() {
	    return new Class[] { String.class };
	}
    }

    private class ShortParser implements Parser<Short> {
	public Short parse( String value ) {
	    return Short.parseShort( value );
	}
	public Class[] getParsedClasses() {
	    return new Class[] { Short.class, Short.TYPE };
	}
    }

    private class IntegerParser implements Parser<Integer> {
	public Integer parse( String value ) {
	    return Integer.parseInt( value );
	}
	public Class[] getParsedClasses() {
	    return new Class[] { Integer.class, Integer.TYPE };
	}
    }

    private class LongParser implements Parser<Long> {
	public Long parse( String value ) {
	    return Long.parseLong( value );
	}
	public Class[] getParsedClasses() {
	    return new Class[] { Long.class, Long.TYPE };
	}
    }
    
    private class FloatParser implements Parser<Float> {
	public Float parse( String value ) {
	    return Float.parseFloat( value );
	}
	public Class[] getParsedClasses() {
	    return new Class[] { Float.class, Float.TYPE };
	}
    }
    
    private class DoubleParser implements Parser<Double> {
	public Double parse( String value ) {
	    return Double.parseDouble( value );
	}
	public Class[] getParsedClasses() {
	    return new Class[] { Double.class, Double.TYPE };
	}
    }

    private class BooleanParser implements Parser<Boolean> {
	public Boolean parse( String value ) {
	    return Boolean.parseBoolean( value );
	}
	public Class[] getParsedClasses() {
	    return new Class[] { Boolean.class, Boolean.TYPE };
	}
    }
    
    private class CharacterParser implements Parser<Character> {
	public Character parse( String value ) throws ParseException {
	    if (value.length()!=1)
		throw new ParseException( "'"+value+"' is not a character.", 0 );
	    return value.charAt( 0 );
	}
	public Class[] getParsedClasses() {
	    return new Class[] { Character.class, Character.TYPE };
	}
    }

    private Map<Class,Parser> parsers;

    private PrimitiveParser() {
	this.parsers = new IdentityHashMap<Class, PrimitiveParser.Parser>( 100 );
	registerParsers();
    }

    private void registerParsers()
    {
	registerParser( new StringParser() );
	registerParser( new ShortParser() );
	registerParser( new IntegerParser() );
	registerParser( new LongParser() );
	registerParser( new FloatParser() );
	registerParser( new DoubleParser() );
	registerParser( new BooleanParser() );
	registerParser( new CharacterParser() );
    }

    public void registerParser( PrimitiveParser.Parser parser ) {
	for (Class c:parser.getParsedClasses()) {
	    if (parsers.containsKey( c )) {
		throw new RuntimeException(
			"The parser for the class '"+c.getCanonicalName()+
			"' has already been registered as '"+parsers.get( c ).getClass().getCanonicalName()+"'.");
	    }
	    parsers.put( c, parser );
	}
    }

    public <E> E parse( String text, Class<E> returnType ) throws ParseException {
	Parser parser = parsers.get( returnType );
	if (parser==null) {
	    throw new RuntimeException(
		    "The parser for the class '"+returnType.getCanonicalName()+
		    "' has not been registered yet.");
	}
	return (E)parser.parse( text );
    }

}
