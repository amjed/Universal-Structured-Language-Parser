/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jellyfish.common.CaseInsensitiveStringComparator;
import jellyfish.tokenizer.sql.SqlTokenizer;

/**
 *
 * @author Xevia
 */
public class Test {
	
	private static class DatabaseObject<ChildDatabaseObject extends DatabaseObject> {
		private String name;
		private Map<String,ChildDatabaseObject> childObjects = new TreeMap<String, ChildDatabaseObject>(new CaseInsensitiveStringComparator());

		public DatabaseObject( String name ) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		protected void registerChild( ChildDatabaseObject object ) {
			childObjects.put( object.getName(), object );
		}

		public Map<String, ChildDatabaseObject> getChildObjects() {
			return childObjects;
		}

		public DatabaseObject findDescendantName(String name[], int index) {
			if (name[index].equalsIgnoreCase( name[index] )) {
				if (index==name.length-1) {
					return this;
				} else {
					ChildDatabaseObject child = childObjects.get( name[index+1] );
					if (child!=null)
						return child.findDescendantName( name, index+1 );
					else
						return null;
				}
			} else
				return null;
		}
	}

	private static class ChildDatabaseObject<ParentDatabaseObject extends DatabaseObject,ChildDatabaseObject extends DatabaseObject> extends DatabaseObject<ChildDatabaseObject> {
		private ParentDatabaseObject parent;

		public ChildDatabaseObject( String name, ParentDatabaseObject parent ) {
			super( name );
			this.parent = parent;
			
			registerSelf();
		}

		private void registerSelf() {
			this.parent.registerChild( this );
		}

		public ParentDatabaseObject getParent() {
			return parent;
		}
		
	}

	private static interface Selection {

	}

	private static interface Source {
		
		Collection<Selection> getSelection();

	}

	private static class Database extends DatabaseObject {

		public Database( String name ) {
			super( name );
		}
		
	}

	private static class Schema extends ChildDatabaseObject<Database,DatabaseObject>  {

		public Schema( String name, Database parent ) {
			super( name, parent );
		}

	}

	private static class Table extends ChildDatabaseObject<Schema,Field> implements Source {

		public Table( String name, Schema parent ) {
			super( name, parent );
		}

		public Collection<Selection> getSelection() {
			return (Collection<Selection>)(Collection)getChildObjects().values();
		}
		
	}

	private static class Field extends ChildDatabaseObject<Table,DatabaseObject> implements Selection {

		public Field( String name, Table parent ) {
			super( name, parent );
		}
		
	}

	private static class Function extends ChildDatabaseObject<Database,DatabaseObject> {

		private int numOfFields;

		public Function( String name, Database parent, int numOfFields ) {
			super( name, parent );
			this.numOfFields = numOfFields;
		}

		public int getNumOfFields() {
			return numOfFields;
		}

		public boolean isAggregate() {
			return false;
		}

	}

	private static class AggregateFunction extends Function {

		public AggregateFunction( String name, Database parent, int numOfFields ) {
			super( name, parent, numOfFields );
		}

		@Override
		public boolean isAggregate() {
			return true;
		}
		

	}
	
	private static class FunctionApplication implements Selection {
		
		private Function function;
		private List<Selection> parameters;

		public FunctionApplication( Function function ) {
			this.function = function;
			this.parameters = new ArrayList<Selection>();
		}

		public List<Selection> getParameters() {
			return parameters;
		}

		public void addParameter(Selection param) {
			if (parameters.size()<function.getNumOfFields()) {
				parameters.add( param );
			} else
				throw new RuntimeException( "Aggregation has too many parameters: "+function );
		}
		
	}

	

	public static void main( String[] args ) {

		Database db = new Database( "jellyfish" );
		
		Schema schema = new Schema( "schema", db);

		Table table1 = new Table( "table1", schema);
		Table table2 = new Table( "table2", schema);

		Field field1 = new Field( "field1", table1);
		Field field2 = new Field( "field2", table1);
		Field field3 = new Field( "field1", table2);
		Field field4 = new Field( "field2", table2); 
		

		String input = "select * from table";
		List<String> tokens = new ArrayList<String>();
		SqlTokenizer sqlTokenizer = new SqlTokenizer();
		sqlTokenizer.tokenize( tokens, input );

		
		
	}

}
