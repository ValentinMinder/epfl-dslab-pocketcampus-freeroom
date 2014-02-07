// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using PocketCampus.Common;
using PocketCampus.Satellite.Models;

namespace PocketCampus.Satellite
{
    /// <summary>
    /// Plumbing to display BeerMenuParts in LongListSelectors.
    /// </summary>
    public sealed class BeerMenuPartsToGroupsConverter : ValueConverter<IDictionary<BeerContainer, BeerMenuPart>, IList<BeerMenuPartAsGroup>>
    {
        public override IList<BeerMenuPartAsGroup> Convert( IDictionary<BeerContainer, BeerMenuPart> value )
        {
            if ( value == null )
            {
                return null;
            }
            return value.Select( p => new BeerMenuPartAsGroup( p.Key, p.Value ) ).ToArray();
        }
    }
}