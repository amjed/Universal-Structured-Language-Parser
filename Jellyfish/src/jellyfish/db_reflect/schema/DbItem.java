/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.db_reflect.schema;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import jellyfish.common.CaseInsensitiveStringComparator;

/**
 *
 * @author Xevia
 */
public class DbItem<ItemParent extends DbItem> implements Comparable<DbItem> {

    private DbCatalog catalog;
    private ItemParent parent;
    private String name;

    private boolean childItemsInitialized;
    private Map<String,DbItem> childItems;

    private String[] fullName;
    private boolean fullNameNeedsRecomputing;
    private String unifiedFullName;

    public DbItem( DbCatalog catalog, String name ) {
        this.catalog = catalog;
        this.name = name;
        this.childItemsInitialized = false;
        this.childItems = Collections.EMPTY_MAP;
        this.fullNameNeedsRecomputing = true;
    }

    public String getName() {
        if (this.isAnonymous())
            return null;
        else
            return name;

    }

    public DbCatalog getCatalog() {
        return catalog;
    }
    
    private synchronized void initChildItems() {
        if (childItemsInitialized) return;
        
        childItems = new TreeMap<String, DbItem>(new CaseInsensitiveStringComparator());

        childItemsInitialized = true;
    }

    protected void registerChild(DbItem child)
    {
        if (!childItemsInitialized) initChildItems();

        if (childItems.containsKey( child.name )) {
            throw new jellyfish.db_reflect.DbLoadException( "The parent item "+this+" already contains a child called '"+child.name+"'" );
        }

        childItems.put( child.name, child );
    }

    public ItemParent getParent() {
        return parent;
    }

    protected void setParent( ItemParent parent ) {
        this.parent = parent;
        this.parent.registerChild( this );
        this.fullNameNeedsRecomputing = true;
    }

    public <Type extends DbItem> Collection<Type> getChildrenOfType(Class<Type> cl) {
        if (!childItemsInitialized)
            return Collections.EMPTY_LIST;

        Collection<Type> coll = new ArrayList<Type>(this.childItems.size());
        for (DbItem child:childItems.values()) {
            if (cl.isAssignableFrom( child.getClass() )) {
                coll.add( (Type)child );
            }
        }
        return coll;
    }

    public <Type extends DbItem> Type getChildByName(String name, Class<Type> cl) {
        if (!childItemsInitialized)
            return null;
        
        DbItem child = childItems.get( name );
        if (child!=null && (cl==null || cl.isAssignableFrom( child.getClass() ))) {
            return (Type)child;
        } else {
            return null;
        }
    }

    public DbItem getChildByName(String name) {
        if (!childItemsInitialized)
            return null;
        
        return childItems.get( name );
    }

    
    private DbItem findChildByFullName(String[] fullName, int i) {
        if (i<fullName.length-1) {
            DbItem child = getChildByName( fullName[i] );
            if (child!=null)
                return child.findChildByFullName( fullName, i+1 );
            else
                return null;
        }
        else
            return getChildByName( fullName[i] );
    }

    public DbItem findChildByFullName(String[] fullName) {
        return findChildByFullName( fullName, 0 );
        /*
        if (this.isAnonymous())
            return findChildByFullName( fullName, 0 );
        else
            if (
                    fullName.length>0 &&
                    (
                        (catalog==null && fullName[0].equalsIgnoreCase( this.name )) ||
                        (catalog!=null &&
                            catalog.simpleUnquote( fullName[0] ).
                                equalsIgnoreCase( catalog.simpleUnquote(this.name))
                        )
                    )
                )
            {
                if (fullName.length==1)
                    return this;
                else
                    return findChildByFullName( fullName, 1 );
            } else
                return null;
         */
    }
    
    private boolean tryMatchPartialName(String[] partialName)
    {
        if (fullNameNeedsRecomputing) computeFullName();

        if (this.isAnonymous()) return false;

        for (int i=0; i<fullName.length; ++i)
        {
            if ( i>partialName.length-1 ) {
                break;
            }

            if (catalog==null) {
                if (!fullName[fullName.length-1-i].
                        equalsIgnoreCase( partialName[partialName.length-1-i] ))
                    return false;
            } else {
                if (!catalog.simpleUnquote( fullName[fullName.length-1-i] ).
                            equalsIgnoreCase( catalog.simpleUnquote(partialName[partialName.length-1-i])))
                    return false;
            }

        }

        return true;
    }

    protected <Type extends DbItem> void findChildrenByPartialName(String[] partialName, List<Type> output, Class<Type> cl)
    {
        for (DbItem child:childItems.values()) {
            if ((cl==null || cl.isAssignableFrom( child.getClass() ))
                    && child.tryMatchPartialName( partialName ))
                output.add( (Type)child );
            child.findChildrenByPartialName( partialName, output, cl );
        }
    }

    public <Type extends DbItem> List<Type> findItemsByPartialName(String[] partialName, Class<Type> cl) {
        if (partialName.length==0)
            return Collections.EMPTY_LIST;
        
        List<Type> items = new ArrayList<Type>(2);

        if ((cl==null || cl.isAssignableFrom( this.getClass() ))
                    && tryMatchPartialName( partialName ))
            items.add( (Type)this );

        findChildrenByPartialName( partialName, items, cl );

        return items;
    }

    public List<DbItem> findItemsByPartialName(String[] partialName) {
        return findItemsByPartialName( partialName, null );
    }
    
    public Collection<DbItem> getChildren() {
        if (childItemsInitialized)
            return Collections.unmodifiableCollection( this.childItems.values() ) ;
        else
            return Collections.EMPTY_LIST;
    }

    protected boolean isAnonymous() {
        return false;
    }

    protected synchronized void computeFullName() {
        if (!fullNameNeedsRecomputing) return;

        int count = 0;
        DbItem item = this;
        while (item!=null && !item.isAnonymous()) {
            ++count;
            item = item.parent;
        }
        fullName = new String[count];
        --count;
        item = this;
        while (item!=null && !item.isAnonymous()) {
            if (catalog!=null)
                fullName[count] = catalog.simpleQuote( item.name ) ;
            else
                fullName[count] = item.name;
            --count;
            item = item.parent;
        }

        StringBuilder bldr = new StringBuilder();
        for (int i=0; i<fullName.length; ++i) {
            if (i>0)
                bldr.append( "." );
            if (catalog!=null)
                bldr.append( catalog.simpleQuote( fullName[i] ) );
            else
                bldr.append( fullName[i] );
        }
        unifiedFullName = bldr.toString();

        fullNameNeedsRecomputing = false;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DbItem<ItemParent> other = (DbItem<ItemParent>)obj;
        if (this.parent != other.parent && (this.parent == null || !this.parent.equals( other.parent ))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals( other.name )) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.parent != null ? this.parent.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        if (fullNameNeedsRecomputing) computeFullName();
        return unifiedFullName;
    }


    public int compareTo( DbItem o ) {
        int i = this.getClass().getCanonicalName().compareTo( o.getClass().getCanonicalName() );
        if (i==0) {
            return this.toString().compareToIgnoreCase( o.toString() );
        } else
            return i;
    }



}
