// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using ThriftSharp;

namespace PocketCampus.Transport
{
    /// <summary>
    /// Cleans station names received from the server (which are from Schildbach's API).
    /// </summary>
    public sealed class StationNameCleaner : ThriftValueConverter<string, string>
    {
        private static readonly Dictionary<string, string> Replacements = new Dictionary<string, string>
        {
            { "Ecublens VD, EPFL", "EPFL" },
            { "Ecublens VD, EPFL Piccard", "EPFL Piccard" },
            { "Ecublens VD, UNIL-Sorge", "UNIL-Sorge" },
            { "Lausanne, Vigie", "Vigie" },
            { "Chavannes-p.-R., UNIL-Dorigny", "UNIL-Dorigny" },
            { "Chavannes-p.-R., UNIL-Mouline", "UNIL-Mouline" }
        };

        protected override string Convert( string value )
        {
            if ( Replacements.ContainsKey( value ) )
            {
                return Replacements[value];
            }
            return value;
        }

        protected override string ConvertBack( string value )
        {
            throw new NotSupportedException();
        }
    }
}