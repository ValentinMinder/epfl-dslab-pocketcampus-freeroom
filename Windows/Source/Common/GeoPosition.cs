// Copyright (c) PocketCampus.Org 2014-15
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
        private const double EarthRadiusInMeters = 6371000; // mean radius, of course
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
        /// Creates a new GeoPosition.
        /// </summary>
        public GeoPosition( double latitude, double longitude )
        {
            Latitude = latitude;
            Longitude = longitude;
        }


        /// <summary>
        /// Computes the distance to another GeoPosition, in meters.
        /// </summary>
        public double DistanceTo( GeoPosition other )
        {
            double lat1 = Latitude * RadianCoeff,
                   lat2 = other.Latitude * RadianCoeff,
                   lon1 = Longitude * RadianCoeff,
                   lon2 = other.Longitude * RadianCoeff;

            return EarthRadiusInMeters * 2 * Math.Asin( Math.Min( 1, Math.Sqrt( ( Math.Pow( Math.Sin( ( lat2 - lat1 ) / 2.0 ), 2.0 ) + Math.Cos( lat1 ) * Math.Cos( lat2 ) * Math.Pow( Math.Sin( ( lon2 - lon1 ) / 2.0 ), 2.0 ) ) ) ) );
        }
    }
}