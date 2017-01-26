/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.codegen;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.internal.frontend.v3_2.IncomparableValuesException;
import org.neo4j.graphdb.Path;

/**
 * Helper class for dealing with orderability in compiled code.
 *
 * <h1>
 * Orderability
 *
 * <a href="https://github.com/opencypher/openCypher/blob/master/cip/1.accepted/CIP2016-06-14-Define-comparability-and-equality-as-well-as-orderability-and-equivalence.adoc">
 *   The Cypher CIP defining orderability
 * </a>
 *
 * <p>
 * Ascending global sort order of disjoint types:
 *
 * <ul>
 *   <li> MAP types
 *    <ul>
 *      <li> Regular map
 *
 *      <li> NODE
 *
 *      <li> RELATIONSHIP
 *    <ul>
 *
 *  <li> LIST OF ANY?
 *
 *  <li> PATH
 *
 *  <li> STRING
 *
 *  <li> BOOLEAN
 *
 *  <li> NUMBER
 *    <ul>
 *      <li> NaN values are treated as the largest numbers in orderability only (i.e. they are put after positive infinity)
 *    </ul>
 *  <li> VOID (i.e. the type of null)
 * </ul>
 *
 * TBD: POINT and GEOMETRY
 */
public class CompiledOrderabilityUtils {
    /**
     * Do not instantiate this class
     */
    private CompiledOrderabilityUtils()
    {
        throw new UnsupportedOperationException(  );
    }

    /**
     * Compare with Cypher orderability semantics for disjoint types
     *
     * @param lhs
     * @param rhs
     * @return
     */
    public static int compare( Object lhs, Object rhs )
    {
        if ( lhs == rhs )
        {
            return 0;
        }
        // null is greater than any other type
        else if ( lhs == null )
        {
            return 1;
        }
        else if ( rhs == null )
        {
            return -1;
        }

        // Compare the types
        // TODO: Test coverage for the Orderability CIP
        SuperType leftType = SuperType.ofValue( lhs );
        SuperType rightType = SuperType.ofValue( rhs );

        int typeComparison = SuperType.TYPE_ID_COMPARATOR.compare( leftType, rightType );
        if ( typeComparison != 0 )
        {
            // Types are different an decides the order
            return typeComparison;
        }

        // TODO Type-specific compare
        if ( lhs.getClass().isAssignableFrom( rhs.getClass() ) &&
             lhs instanceof Comparable && rhs instanceof Comparable )
        {
            return ((Comparable) lhs).compareTo( rhs );
        }

        throw new IncomparableValuesException( lhs.getClass().getSimpleName(), rhs.getClass().getSimpleName() );
    }

    public enum SuperType
    {
        MAP( 0 ),
        NODE( 1 ),
        RELATIONSHIP( 2 ),
        LIST( 3 ),
        ARRAY( 3 ), // Same order as LIST
        PATH( 4 ),
        STRING( 5 ),
        BOOLEAN( 6 ),
        NUMBER( 7 ),
        VOID( 8 );

        public final int typeId;

        SuperType( int typeId )
        {
            this.typeId = typeId;
        }

        public boolean isSuperTypeOf( Object value )
        {
            return this == ofValue( value );
        }

        public static SuperType ofValue( Object value )
        {
            if ( value instanceof String )
            {
                return STRING;
            }
            else if ( value instanceof Number )
            {
                return NUMBER;
            }
            else if ( value instanceof Boolean )
            {
                return BOOLEAN;
            }
            else if ( value instanceof Character )
            {
                return STRING;
            }
            else if ( value instanceof Map<?,?> )
            {
                return MAP;
            }
            else if ( value instanceof List<?> )
            {
                return LIST;
            }
            else if ( value instanceof NodeIdWrapper )
            {
                return NODE;
            }
            else if ( value instanceof RelationshipIdWrapper )
            {
                return RELATIONSHIP;
            }
            else if ( value instanceof Path )
            {
                return PATH;
            }
            else if ( value.getClass().isArray() )
            {
                return ARRAY;
            }
            return VOID;
        }

        public static Comparator<SuperType> TYPE_ID_COMPARATOR = new Comparator<SuperType>()
        {
            @Override
            public int compare( SuperType left, SuperType right )
            {
                return left.typeId - right.typeId;
            }
        };
    }
}
