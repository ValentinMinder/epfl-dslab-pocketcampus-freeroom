// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Common
{
    /// <summary>
    /// A position on Earth (not including altitude), and an optional heading.
    /// </summary>
    public sealed class GeoPosition
    {
        // Constants for distance computation.
        private const double EarthRadiusInKilometers = 6367.0;
        private const double RadianCoeff = Math.PI / 180;

        /// <summary>
        /// Gets the latitude.
        /// </summary>
        public double Latitude { get; private set; }

        /// <summary>
        /// Gets the longitude.
        /// </summary>
        public double Longitude { get; private set; }

        /// <summary>
        /// Gets the heading, if there is one.
        /// </summary>
        public double? Heading { get; private set; }


        /// <summary>
        /// Creates a new GeoPosition.
        /// </summary>
        public GeoPosition( double latitude, double longitude, double? heading = null )
        {
            Latitude = latitude;
            Longitude = longitude;
            Heading = heading;
        }


        /// <summary>
        /// Computes the distance to another GeoPosition.
        /// </summary>
        public double DistanceTo( GeoPosition other )
        {
            double lat1 = this.Latitude * RadianCoeff,
                   lat2 = other.Latitude * RadianCoeff,
                   lon1 = this.Longitude * RadianCoeff,
                   lon2 = other.Longitude * RadianCoeff;

            return EarthRadiusInKilometers * 2 * Math.Asin( Math.Min( 1, Math.Sqrt( ( Math.Pow( Math.Sin( ( lat2 - lat1 ) / 2.0 ), 2.0 ) + Math.Cos( lat1 ) * Math.Cos( lat2 ) * Math.Pow( Math.Sin( ( lon2 - lon1 ) / 2.0 ), 2.0 ) ) ) ) );
        }
    }
}