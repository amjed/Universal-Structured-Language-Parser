/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jellyfish.matcher;

import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jellyfish.common.Common;
import jellyfish.common.Pair;
import jellyfish.common.PatternExtractor;

/**
 *
 * @author Umran
 */
public class AliasTreeNode
		implements Comparable<AliasTreeNode>
{

	private static final PatternExtractor ALIAS_EXTRACTOR = new PatternExtractor(
			"([^\\[])\\[([0-9]+)\\]" );

//    public static final String ROOT_ALIAS_NAME  = "ROOT";
	public static AliasTreeNode createRoot( String rootName ) {
		return new AliasTreeNode( rootName, 0 )
		{
			@Override
			public boolean isRoot() {
				return true;
			}

			@Override
			public boolean isNormalNode() {
				return false;
			}
		};
	}
	
	public static AliasTreeNode createSysInc() {
		return new AliasTreeNode( "SYS_INC", 2 )
		{
			@Override
			public boolean isSysInc() {
				return true;
			}

			@Override
			public boolean isNormalNode() {
				return false;
			}
		};
	}

	public static Pair<AliasTreeNode, Map<AliasTreeNode, AliasTreeNode>> copyAliasTree(
			AliasTreeNode root, AliasTreeNode newParent ) {
		Map<AliasTreeNode, AliasTreeNode> origToCopyMapping = new IdentityHashMap<AliasTreeNode, AliasTreeNode>();
		AliasTreeNode newRoot = null;
		if (newParent==null)
			newRoot = AliasTreeNode.createRoot( root.name );
		else {
			newRoot = new AliasTreeNode( root.name );
			newRoot.setParent( newParent );
		}
		origToCopyMapping.put( root, newRoot );
		newRoot.copySubTree( root, origToCopyMapping );
		return Pair.create( newRoot, origToCopyMapping );
	}
	
	private static int hashCodeIncr = 3;
	private int hashCode;
	private String name;
	private int index;
	private AliasTreeNode actualParent;	// actual parent of this node
	private AliasTreeNode normalParent;	// first parent that is normal (i.e. not sys-inc) or root
	private boolean childrenInitialized;
	private List<AliasTreeNode> children;
	private Map<String, List<AliasTreeNode>> childNames;
	private String completeName[];

	private AliasTreeNode( String name, int hashCode ) {
		this.hashCode = hashCode;
		this.name = Common.firstCharToUpper( name );
		this.actualParent = null;
		this.normalParent = null;
		this.index = 0;

		this.childrenInitialized = false;
		this.children = Collections.EMPTY_LIST;
		this.childNames = Collections.EMPTY_MAP;

		this.completeName = computeCompleteName();
	}

	public AliasTreeNode( String name ) {
		this( name, hashCodeIncr++ );
	}

	private void copySubTree( AliasTreeNode otherRoot,
							  Map<AliasTreeNode, AliasTreeNode> origToCopyMapping ) {
		ArrayDeque<Pair<AliasTreeNode,AliasTreeNode>> que = new ArrayDeque<Pair<AliasTreeNode,AliasTreeNode>>(100);

		que.add( Pair.create(this,otherRoot) );

		while (!que.isEmpty()) {
			Pair<AliasTreeNode,AliasTreeNode> head = que.remove();
			AliasTreeNode thisParent = head.getFirst();
			AliasTreeNode otherParent = head.getSecond();

			for ( AliasTreeNode otherChild : otherParent.children ) {
				AliasTreeNode thisChild = new AliasTreeNode( otherChild.name );
				thisChild.setParent( thisParent );
				origToCopyMapping.put( otherChild, thisChild );

				if (!otherChild.getChildren().isEmpty()) {
					que.add( Pair.create(thisChild, otherChild) );
				}
			}
		}

	}

	private synchronized void initChildList() {
		if ( childrenInitialized ) {
			return;
		}

		children = new ArrayList( 10 );
		childNames = new TreeMap( new jellyfish.common.CaseInsensitiveStringComparator() );

		childrenInitialized = true;
	}

	/*
	 * If true, means this is a root node.
	 */
	public boolean isRoot() {
		assert(actualParent!=null);
		return false;
	}

	/*
	 * If true, means this node is a SYS-INC node (i.e. space holder nodes created by the system)
	 */
	public boolean isSysInc() {
		return false;
	}

	/*
	 * If true, means this node is not a system node i.e. root or SYS-INC
	 */
	public boolean isNormalNode() {
		return true;
	}

	public Set<String> getChildAliasSet() {
		return childNames.keySet();
	}

	public List<AliasTreeNode> getChildrenWithAlias( String alias ) {
		return childNames.get( alias );
	}

	public List<AliasTreeNode> getChildren() {
		if ( !childrenInitialized ) {
			return Collections.EMPTY_LIST;
		} else {
			return Collections.unmodifiableList( children );
		}
	}

	public String getName() {
		return name;
	}

	public AliasTreeNode getActualParent() {
		return actualParent;
	}

	public AliasTreeNode getNormalParent() {
		return normalParent;
	}

	public int getIndex() {
		return index;
	}

	public AliasTreeNode getRoot() {
		if ( normalParent != null ) {
			return normalParent.getRoot();
		} else {
			return this;
		}
	}

	public void setParent( AliasTreeNode parent ) {
		if ( this.normalParent != null ) {
			this.normalParent.unregisterChild( this );
		}
		if ( this.actualParent != null && this.actualParent!=this.normalParent ) {
			this.actualParent.unregisterChild( this );
		}

		this.actualParent = parent;
		this.normalParent = computeNormalParent( parent );

		if ( this.normalParent != null ) {
			this.index = this.normalParent.registerChild( this );
		}
		if ( this.actualParent != null && this.actualParent!=this.normalParent ) {
			this.actualParent.registerChild( this );
		}

		this.completeName = computeCompleteName();
	}

	private AliasTreeNode computeNormalParent(AliasTreeNode actualParent) {
		AliasTreeNode _nonSysIncParent = actualParent;
		if (!_nonSysIncParent.isRoot() && _nonSysIncParent.isSysInc()) {
			_nonSysIncParent = _nonSysIncParent.getNormalParent();
		}
		return _nonSysIncParent;
	}

	/*
	 * Registers a child and returns the index of the child
	 */
	private int registerChild( AliasTreeNode child ) {
		if (child.isSysInc())
			return 0;

		if ( !childrenInitialized ) {
			initChildList();
		}

		children.add( child );
		List<AliasTreeNode> aliasTreeNodes = childNames.get( child.name );
		if ( aliasTreeNodes == null ) {
			aliasTreeNodes = new ArrayList<AliasTreeNode>( 1 );
			childNames.put( child.name, aliasTreeNodes );
		}

		synchronized ( aliasTreeNodes ){
			int childIndex = aliasTreeNodes.size();
			aliasTreeNodes.add( child );
			return childIndex;
		}
	}

	private void unregisterChild( AliasTreeNode child ) {
		assert children != null;
		assert childNames != null;

		children.remove( child );
		childNames.remove( child.name );
	}

	public List<AliasTreeNode> findChild( String name ) {
		if ( name.isEmpty() || childNames == null ) {
			return Collections.EMPTY_LIST;
		} else {
			return childNames.get( name );
		}
	}

	public String getIndexedName() {
		if ( normalParent != null && normalParent.findChild( name ).size() > 1 ) {
			return name + "[" + index + "]";
		} else {
			return name;
		}
	}

	private synchronized String[] computeCompleteName() {
		int len = 1;
		String parentCompleteName[] = null;

		if ( normalParent != null ) {
			parentCompleteName = normalParent.getCompleteName();
			len = parentCompleteName.length + 1;
		}

		String[] myCompleteName = new String[len];

		if ( parentCompleteName != null ) {
			for ( int i = 0; i < parentCompleteName.length; ++i ) {
				myCompleteName[i] = parentCompleteName[i];
			}
		}
		myCompleteName[myCompleteName.length - 1] = name;

		return myCompleteName;
	}

	public String[] getCompleteName() {
		return completeName;
	}

	public boolean hasCompleteName( String completeName ) {
		String namePortions[] = completeName.split( "\\." );
		String thisName[] = getCompleteName();

		if ( namePortions.length != thisName.length ) {
			return false;
		}

		for ( int i = 0; i < thisName.length; ++i ) {
			if ( !namePortions[i].equalsIgnoreCase( thisName[i] ) ) {
				return false;
			}
		}

		return true;
	}

	private AliasTreeNode internGetSpecificAlias( String[] completeName, int index ) {
		AliasTreeNode selectedChild = null;

		List<String> indexStrs = new ArrayList<String>( 1 );
		ALIAS_EXTRACTOR.extractMatchingGroups( completeName[index], indexStrs );
		if ( indexStrs.size() <= 1 ) {
			List<AliasTreeNode> foundChildren = findChild( indexStrs.get( 0 ).trim() );
			if ( !foundChildren.isEmpty() ) {
				selectedChild = foundChildren.get( 0 );
			}
		} else {
			List<AliasTreeNode> foundChildren = findChild( indexStrs.get( 0 ).trim() );
			int i = Integer.parseInt( indexStrs.get( 1 ).trim() );
			if ( i >= 0 && i < foundChildren.size() ) {
				selectedChild = foundChildren.get( i );
			}
		}

		if ( selectedChild == null ) {
			return null;
		}

		if ( index == completeName.length - 1 ) {
			return selectedChild;
		} else {
			return selectedChild.internGetSpecificAlias( completeName, index + 1 );
		}
	}

	public AliasTreeNode getSpecificAlias( String[] completeName ) {
		return internGetSpecificAlias( completeName, 0 );
	}

	private void internGetGeneralAlias( String[] completeName, int index, List<AliasTreeNode> output ) {
		String indexedName = completeName[index];

		List<AliasTreeNode> nodes = childNames.get( indexedName );

		if ( index == completeName.length - 1 ) {
			output.addAll( nodes );
		} else {
			for ( AliasTreeNode node : nodes ) {
				node.internGetGeneralAlias( completeName, index + 1, output );
			}
		}
	}

	public List<AliasTreeNode> getAliases( String[] generalCompleteName ) {
		List<AliasTreeNode> output = new ArrayList<AliasTreeNode>();
		internGetGeneralAlias( generalCompleteName, 0, output );
		return output;
	}

	@Override
	public String toString() {
		return (normalParent == null ? "/" : "") + Arrays.toString( getCompleteName() ) + ":" + hashCode;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		final AliasTreeNode other = (AliasTreeNode) obj;
		if ( this.hashCode != other.hashCode ) {
			return false;
		}
		return true;
	}

	@Override
	final public int hashCode() {
		return this.hashCode;
	}

	public int getTreeSize() {
		int treeSize = 1;

		for ( AliasTreeNode v : children ) {
			treeSize += v.getTreeSize();
		}

		return treeSize;
	}

	private void internGetAllSubTreeNodes( List<AliasTreeNode> nodes ) {
		nodes.add( this );
		if ( children != null ) {
			for ( AliasTreeNode node : children ) {
				node.internGetAllSubTreeNodes( nodes );
			}
		}
	}

	private void internGetTreeLeftNodes( List<AliasTreeNode> nodes ) {
		if ( this.normalParent != null ) {
			this.normalParent.internGetTreeLeftNodes( nodes );

			for ( AliasTreeNode node : this.normalParent.children ) {
				if ( node.equals( this ) ) {
					break;
				}
				node.internGetAllSubTreeNodes( nodes );
			}
		}

		nodes.add( this );
	}

	//  returns all nodes that lie to the left of this node...
	//  e.g.                <Root>
	//                 <a>    <b>    <c>
	//            <d>   <e>   <f>   <g> <h>
	//      if this node is node b, then the returned nodes
	//      will be: Root, a, d, e, b, f
	public List<AliasTreeNode> getTreeLeftNodes() {
		if ( this.normalParent == null ) {
			return Collections.EMPTY_LIST;
		}

		List<AliasTreeNode> nodes = new ArrayList<AliasTreeNode>();
		internGetTreeLeftNodes( nodes );
		return nodes;
	}

	private void internGetAllSubTreeLeafNodes( List<AliasTreeNode> nodes ) {
		if ( children != null && !children.isEmpty() ) {
			for ( AliasTreeNode node : children ) {
				node.internGetAllSubTreeNodes( nodes );
			}
		} else {
			nodes.add( this );
		}
	}

	private void internGetTreeLeftLeafNodes( List<AliasTreeNode> nodes ) {
		if ( this.normalParent != null ) {
			this.normalParent.internGetTreeLeftLeafNodes( nodes );

			for ( AliasTreeNode node : this.normalParent.children ) {
				if ( node.equals( this ) ) {
					break;
				}
				node.internGetAllSubTreeLeafNodes( nodes );
			}
		}

		if ( this.children == null || this.children.isEmpty() ) {
			nodes.add( this );
		}
	}

	//  returns all nodes that lie to the left of this node...
	//  e.g.                <Root>
	//                 <a>    <b>    <c>
	//            <d>   <e>   <f>   <g> <h>
	//      if this node is node b, then the returned nodes
	//      will be: d, e, f
	public List<AliasTreeNode> getTreeLeftLeafNodes() {
		if ( this.normalParent == null ) {
			return Collections.EMPTY_LIST;
		}

		List<AliasTreeNode> nodes = new ArrayList<AliasTreeNode>();
		internGetTreeLeftLeafNodes( nodes );
		return nodes;
	}

	public int compareTo( AliasTreeNode other ) {
		String[] thisName = this.getCompleteName();
		String[] otherName = other.getCompleteName();

		int i = thisName.length - otherName.length;
		if ( i == 0 ) {
			for ( int j = 0; j < thisName.length; ++j ) {
				i = thisName[j].compareTo( otherName[j] );
				if ( i != 0 ) {
					break;
				}
			}
		}

		return i;
	}
}
