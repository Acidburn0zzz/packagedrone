/*******************************************************************************
 * Copyright (c) 2016 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.packagedrone.repo.channel.search;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.packagedrone.repo.MetaKey;

/**
 * Methods for building predicates
 */
public final class Predicates
{
    /**
     * Matches zero or more of any character in a like pattern
     */
    public static final String LIKE_WILDCARD_MANY = "%";

    /**
     * Matches any single one character in a like pattern
     */
    public static final String LIKE_WILDCARD_SINGLE = "_";

    private Predicates ()
    {
    }

    public static Predicate and ( final Predicate... predicates )
    {
        return new And ( predicates );
    }

    public static Predicate and ( final Collection<Predicate> predicates )
    {
        return new And ( predicates );
    }

    public static Predicate or ( final Predicate... predicates )
    {
        return new Or ( predicates );
    }

    public static Predicate not ( final Predicate predicate )
    {
        return new Not ( predicate );
    }

    public static Predicate isNull ( final Value value )
    {
        return new IsNull ( value );
    }

    public static Predicate isNotNull ( final Value value )
    {
        return not ( isNull ( value ) );
    }

    public static Literal literal ( final String value )
    {
        return new Literal ( value );
    }

    public static Value attribute ( final MetaKey key )
    {
        return new MetaKeyValue ( key );
    }

    public static Predicate equal ( final Value value1, final Value value2 )
    {
        return new Equal ( value1, value2 );
    }

    public static Predicate equal ( final MetaKey key, final String literal )
    {
        return new Equal ( attribute ( key ), literal ( literal ) );
    }

    public static Predicate equal ( final MetaKey key, final Optional<String> literal )
    {
        if ( literal.isPresent () )
        {
            return equal ( attribute ( key ), literal ( literal.get () ) );
        }
        else
        {
            return isNull ( attribute ( key ) );
        }
    }

    public static Predicate like ( final Value value, final Literal pattern )
    {
        return new Like ( value, pattern, true );
    }

    public static Predicate like ( final MetaKey key, final String literal )
    {
        return new Like ( attribute ( key ), literal ( literal ), true );
    }

    public static Predicate like ( final Value value, final Literal pattern, final boolean caseSensitive )
    {
        return new Like ( value, pattern, caseSensitive );
    }

    public static Predicate like ( final MetaKey key, final String literal, final boolean caseSensitive )
    {
        return new Like ( attribute ( key ), literal ( literal ), caseSensitive );
    }

}
