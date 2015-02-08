// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Runtime.InteropServices.WindowsRuntime;
using System.Text;
using PocketCampus.Common.Services;
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
                    _current = TokenToString( token );
                }
                return _current;
            }
        }


        private static string TokenToString( HardwareToken token )
        {
            var bytes = token.Id.ToArray();
            return Encoding.UTF8.GetString( bytes, 0, bytes.Length );
        }
    }
}