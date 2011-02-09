/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.triplestore.xml.parsers;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import jellyfish.common.PatternExtractor;
import jellyfish.triplestore.model.FloatValue;
import jellyfish.triplestore.model.IntegerValue;
import jellyfish.triplestore.model.StringValue;
import jellyfish.triplestore.model.Triple;
import jellyfish.triplestore.model.Value;
import jellyfish.xml.XmlNodeInfo;
import org.w3c.dom.Element;

/**
 *
 * @author Xevia
 */
public class ValueParser
		extends jellyfish.xml.XmlNodeParser<Value, Triple>
{

	private static final PatternExtractor INT_PATTERN = new PatternExtractor( "[+-]?\\d+" );
	private static final PatternExtractor FLOAT_PATTERN = new PatternExtractor( "[+-]?\\d+\\.\\d+" );
	private static final Set<Integer> VALID_INDEXES = new HashSet<Integer>( Arrays.asList( 0, 2 ) );

	@Override
	public boolean requiresExactParentIndex() {
		return true;
	}

	@Override
	public Set<Integer> getValidParentIndexes() {
		return VALID_INDEXES;
	}

	@Override
	public Value parse( XmlNodeInfo<Triple> nodeInfo ) {
		Element element = (Element) nodeInfo.getNode();

		String value = element.getTextContent();
		value = value.trim();

		String defValueType = "string";
		if ( FLOAT_PATTERN.matches( value ) ) {
			defValueType = "float";
		} else if ( INT_PATTERN.matches( value ) ) {
			defValueType = "int";
		}

		String valueType = getAttributeOrDefault( nodeInfo, "type", defValueType );

		int valueTypeInt = -1;
		if ( valueType.equalsIgnoreCase( "string" ) ) {
			valueTypeInt = 0;
		}
		if ( valueType.equalsIgnoreCase( "int" ) ) {
			valueTypeInt = 1;
		}
		if ( valueType.equalsIgnoreCase( "float" ) ) {
			valueTypeInt = 2;
		}

		Value val = null;
		switch ( valueTypeInt ) {
			case 0: {
				val = new StringValue( value );
				break;
			}
			case 1: {
				long intVal = Integer.parseInt( value );
				val = new IntegerValue( intVal );
				break;
			}
			case 2: {
				double floatVal = Double.parseDouble( value );
				val = new FloatValue( floatVal );
			}
			default:
				throw new RuntimeException( "Value type defined in location: " + nodeInfo.
						getLocation() +
											" Value can either be of type string, int or float." );

		}

		switch ( nodeInfo.getNodeIndex() ) {
			case 0: {
				assert (nodeInfo.getParentObject().getSubject() == null);
				nodeInfo.getParentObject().setSubject( val );
				break;
			}
			case 2: {
				assert (nodeInfo.getParentObject().getObject() == null);
				nodeInfo.getParentObject().setObject( val );
				break;
			}
		}

		return val;
	}
}
