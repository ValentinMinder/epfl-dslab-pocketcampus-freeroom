// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using ThriftSharp;

namespace PocketCampus.Transport
{
    /// <summary>
    /// Cleans line names received from the server (which are directly from the Schildbach API).
    /// </summary>
    public sealed class LineNameCleaner : ThriftValueConverter<string, string>
    {
        // If a line name begins with a key, remove the key and prepend the value
        private static readonly Dictionary<string, string> Prefixes = new Dictionary<string, string>
        {
            { "UMetm", "M" },
            { "UMm", "M" },
            { "BBus", "Bus " }
        };

        // If a line name contains one of these, remove everything else
        private static readonly string[] SingleNames = { "RE", "IR", "ICN", "IC", "SS" };

        private const string UselessPrefix = "I";

        protected override string Convert( string value )
        {
            // First check for known prefixes that require numbers to be kept as-is
            foreach ( var pair in Prefixes )
            {
                if ( value.StartsWith( pair.Key, StringComparison.OrdinalIgnoreCase ) )
                {
                    return pair.Value + value.Substring( pair.Key.Length );
                }
            }

            // Then look for known names like train names, where numbers don't matter
            var name = SingleNames.FirstOrDefault( value.Contains );
            if ( name != null )
            {
                return name;
            }

            // Finally, remove the prefix if there's one and remove all numbers
            if ( value.StartsWith( UselessPrefix ) )
            {
                value = value.Substring( UselessPrefix.Length );
            }

            return new string( value.ToCharArray().Where( c => !char.IsDigit( c ) ).ToArray() );
        }

        protected override string ConvertBack( string value )
        {
            throw new NotSupportedException();
        }
    }
}