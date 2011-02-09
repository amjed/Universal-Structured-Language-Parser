/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.editor.model;

import jellyfish.common.ObservableListImpl;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 *
 * @author Xevia
 */
public class ConnectorCustomizer implements DescriptorCustomizer {

    public void customize(ClassDescriptor cd) throws Exception {
        DatabaseMapping databaseMapping = cd.getMappingForAttributeName("connectorPoints");
        CollectionMapping collectionMapping = (CollectionMapping)databaseMapping;
        collectionMapping.setListOrderFieldName("index");
//        collectionMapping.useCollectionClass(ObservableList.class);
    }

}
