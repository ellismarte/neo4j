/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
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
package org.neo4j.cypher.operations;

import org.opencypher.v9_0.util.EntityNotFoundException;

import org.neo4j.internal.kernel.api.CursorFactory;
import org.neo4j.internal.kernel.api.NodeCursor;
import org.neo4j.internal.kernel.api.PropertyCursor;
import org.neo4j.internal.kernel.api.RelationshipScanCursor;
import org.neo4j.internal.kernel.api.Transaction;
import org.neo4j.values.storable.Value;
import org.neo4j.values.storable.Values;

/**
 * This class contains static helper methods for expressions interacting with the database
 */
@SuppressWarnings( "unused" )
public final class CypherDbAccess
{
    private CypherDbAccess()
    {
        throw new UnsupportedOperationException( "Do not instantiate" );
    }

    //data access
    public static Value nodeProperty( Transaction tx, long node, int property )
    {
        CursorFactory cursors = tx.cursors();
        try ( NodeCursor nodes = cursors.allocateNodeCursor();
              PropertyCursor properties = cursors.allocatePropertyCursor() )
        {
            tx.dataRead().singleNode( node, nodes );
            if ( nodes.next() )
            {
                nodes.properties( properties );
                return property( properties, property );
            }
            if ( tx.dataRead().nodeDeletedInTransaction( node ) )
            {
                throw new EntityNotFoundException(
                        String.format( "Node with id %d has been deleted in this transaction", node ), null );
            }
            return Values.NO_VALUE;
        }
    }

    public static Value relationshipProperty( Transaction tx, long relationship, int property )
    {
        CursorFactory cursors = tx.cursors();
        try ( RelationshipScanCursor relationships = cursors.allocateRelationshipScanCursor();
              PropertyCursor properties = cursors.allocatePropertyCursor() )
        {
            tx.dataRead().singleRelationship( relationship, relationships );
            if ( relationships.next() )
            {
                relationships.properties( properties );
                return property( properties, property );
            }
            if ( tx.dataRead().relationshipDeletedInTransaction( relationship ) )
            {
                throw new EntityNotFoundException(
                        String.format( "Relationship with id %d has been deleted in this transaction", relationship ), null );
            }
            return Values.NO_VALUE;
        }
    }

    private static Value property( PropertyCursor properties, int property )
    {
        while ( properties.next() )
        {
            if ( properties.propertyKey() == property )
            {
                return properties.propertyValue();
            }
        }
        return Values.NO_VALUE;
    }
}
