// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using PocketCampus.Satellite.Models;
using PocketCampus.Satellite.Resources;

// Plumbing to display BeerMenuParts in LongListSelectors.

namespace PocketCampus.Satellite
{
    public sealed class BeerMenuPartAsGroup : List<BeerMenuPartAsGroup.BeerTypeGroup>
    {
        public BeerContainer Container { get; private set; }

        public BeerMenuPartAsGroup( BeerContainer container, BeerMenuPart part )
        {
            Container = container;

            if ( part.BeersOfTheMonth.Any() )
            {
                Add( new BeerTypeGroup( PluginResources.BeersOfTheMonth, part.BeersOfTheMonth.OrderBy( b => b.Name ) ) );
            }

            AddRange( part.Beers.Select( p => new BeerTypeGroup( p.Key, p.Value.OrderBy( b => b.Name ) ) ).OrderBy( b => b.Type ) );
        }

        public sealed class BeerTypeGroup : List<Beer>
        {
            public string Type { get; private set; }

            public BeerTypeGroup( string type, IEnumerable<Beer> beers )
                : base( beers )
            {
                Type = type;
            }
        }
    }
}