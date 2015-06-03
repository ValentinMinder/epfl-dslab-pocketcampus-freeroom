// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections;
using System.Collections.Generic;
using System.Linq;
using PocketCampus.Common;
using PocketCampus.Satellite.Models;
using Windows.ApplicationModel.Resources;

namespace PocketCampus.Satellite
{
    public sealed class BeerMenuPartToGroupConverter : ValueConverter<BeerMenuPart, IEnumerable<IGrouping<string, Beer>>>
    {
        private static readonly string OfTheMonthText =
            ResourceLoader.GetForViewIndependentUse( "PocketCampus.Satellite.WindowsRuntime/Main" ).GetString( "BeersOfTheMonth" );

        public override IEnumerable<IGrouping<string, Beer>> Convert( BeerMenuPart value )
        {
            var groups = new List<IGrouping<string, Beer>>();

            if ( value.BeersOfTheMonth.Any() )
            {
                groups.Add( new Group( OfTheMonthText, value.BeersOfTheMonth ) );
            }

            groups.AddRange( value.Beers.Select( p => new Group( p.Key, p.Value ) ) );

            return groups;
        }

        private sealed class Group : IGrouping<string, Beer>
        {
            private readonly string _key;
            private readonly Beer[] _beers;


            public string Key { get { return _key; } }


            public Group( string key, Beer[] beers )
            {
                _key = key;
                _beers = beers;
            }


            public IEnumerator<Beer> GetEnumerator()
            {
                return ( (IEnumerable<Beer>) _beers ).GetEnumerator();
            }

            IEnumerator IEnumerable.GetEnumerator()
            {
                return GetEnumerator();
            }
        }
    }
}