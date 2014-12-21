using System.Collections.Generic;
using System.Linq;

namespace PocketCampus.Map
{
    public static class NameNormalizer
    {
        private const string IgnoredNamePrefix = "Auditoire ";

        public static bool AreRoomNamesEqual( string name1, string name2 )
        {
            return NormalizeName( name1 ).SequenceEqual( NormalizeName( name2 ) );
        }

        private static IEnumerable<char> NormalizeName( string name )
        {
            return name.Replace( IgnoredNamePrefix, "" )
                       .ToUpperInvariant()
                       .ToCharArray()
                       .Where( c => !char.IsWhiteSpace( c ) );
        }
    }
}