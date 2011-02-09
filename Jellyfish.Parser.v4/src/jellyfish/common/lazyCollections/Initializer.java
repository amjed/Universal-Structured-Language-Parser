package jellyfish.common.lazyCollections;

public interface Initializer<CollectionType> {

    CollectionType initialize();
}
