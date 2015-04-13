// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Services;
using Windows.Security.Cryptography;
using Windows.Security.Cryptography.Core;
using Windows.System.Profile;

namespace PocketCampus.Main.Services
{
    public sealed class DeviceIdentifier : IDeviceIdentifier
    {
        private static string _current;

        public string Current
        {
            get
            {
                if ( _current == null )
                {
                    var token = HardwareIdentification.GetPackageSpecificToken( null );
                    // Hash it with MD5 to get a length <50, that's what the server wants
                    var alg = HashAlgorithmProvider.OpenAlgorithm( HashAlgorithmNames.Md5 );
                    var hashed = alg.HashData( token.Id );
                    _current = CryptographicBuffer.EncodeToHexString( hashed );
                }
                return _current;
            }
        }
    }
}