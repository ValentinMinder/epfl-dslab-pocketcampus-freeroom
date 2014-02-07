// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport
{
    /// <summary>
    /// Converts coordinates received from the server, which are from the Schildbach API.
    /// </summary>
    public sealed class SchildbachCoordinateConverter : ThriftValueConverter<int, double>
    {
        private const double Factor = 1000000;

        protected override double Convert( int value )
        {
            return value / Factor;
        }

        protected override int ConvertBack( double value )
        {
            return (int) ( value * Factor );
        }
    }
}