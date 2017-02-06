/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.causalclustering.load_balancing;

import org.neo4j.helpers.AdvertisedSocketAddress;

/**
 * This class binds a certain role with an address and
 * thus defines a reachable endpoint with defined capabilities.
 */
class EndPoint
{
    private final AdvertisedSocketAddress address;
    private final Role role;

    public String address()
    {
        return address.toString();
    }

    private EndPoint( AdvertisedSocketAddress address, Role role )
    {
        this.address = address;
        this.role = role;
    }

    public static EndPoint write( AdvertisedSocketAddress address )
    {
        return new EndPoint( address, Role.WRITE );
    }

    public static EndPoint read( AdvertisedSocketAddress address )
    {
        return new EndPoint( address, Role.READ );
    }

    static EndPoint route( AdvertisedSocketAddress address )
    {
        return new EndPoint( address, Role.ROUTE );
    }

    @Override
    public String toString()
    {
        return "EndPoint{" + "address=" + address + ", role=" + role + '}';
    }
}
