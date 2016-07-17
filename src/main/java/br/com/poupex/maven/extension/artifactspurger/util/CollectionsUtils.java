package br.com.poupex.maven.extension.artifactspurger.util;

import java.util.Collection;
import java.util.Collections;

public abstract class CollectionsUtils {

	public static <T> boolean hasAny(Collection<T> collection, Collection<? extends T> elements) {
		return !Collections.disjoint(collection, elements);
	}

}
