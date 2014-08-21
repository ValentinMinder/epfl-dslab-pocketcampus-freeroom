// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.IO;
using System.Reflection;

namespace PocketCampus.Common
{
    /// <summary>
    /// Utility class to read text files in assemblies.
    /// </summary>
    public static class AssemblyReader
    {
        /// <summary>
        /// Gets the entire text of the file with the specified name in the specified assembly.
        /// </summary>
        public static string GetText( Assembly assembly, string fileName )
        {
            try
            {
                using ( StreamReader reader = new StreamReader( assembly.GetManifestResourceStream( fileName ) ) )
                {
                    return reader.ReadToEnd();
                }
            }
            catch
            {
                return string.Empty;
            }
        }
    }
}