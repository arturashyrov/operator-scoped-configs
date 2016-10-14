package se.tain.autoconfigure;

/**
 * There should be one and only one bran instance of this interface.
 * It will be used to define a scope of config.
 *
 * Scope is a string which is prepended to actual config path.
 * For example if config prefix we are looking for is foo.bar
 * and context evaluated via lookup method as xxx - then actual config for extracting will be
 * xxx.foo.bar
 */
public interface RuntimeConfigScope {
    default String lookup() { return null; }
}
