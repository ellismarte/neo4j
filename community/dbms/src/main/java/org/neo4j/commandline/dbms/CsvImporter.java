/*
 * Copyright (c) 2002-2016 "Neo Technology,"
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
package org.neo4j.commandline.dbms;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.neo4j.kernel.configuration.Config;
import org.neo4j.tooling.ImportTool;

import static org.neo4j.dbms.DatabaseManagementSystemSettings.database_path;

class CsvImporter implements Importer
{
    private final String[] args = new String[]{};

    CsvImporter( String[] args, Config config )
    {
        List argsList = Arrays.asList( args );
        argsList.add( String.format( "--into=%s", config.get( database_path ) ) );
        argsList.toArray( this.args );
    }

    @Override
    public void doImport() throws IOException
    {
        ImportTool.main( args );
    }
}
