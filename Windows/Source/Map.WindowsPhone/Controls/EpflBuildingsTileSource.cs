// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using Microsoft.Phone.Maps.Controls;
using PocketCampus.Map.Models;

namespace PocketCampus.Map.Controls
{
    /// <summary>
    /// Adapts EpflBuildingsSource to a Windows Phone TileSource.
    /// </summary>
    public sealed class EpflBuildingsTileSource : TileSource
    {
        /// <summary>
        /// Gets or sets the buildings level.
        /// </summary>
        public int Level { get; set; }

        /// <summary>
        /// Gets an URI for the image corresponding to the specified X and Y coordinates, as well as the specified zoom level.
        /// </summary>
        public override Uri GetUri( int x, int y, int zoomLevel )
        {
            return EpflBuildingsSource.GetUri( Level, x, y, zoomLevel );
        }
    }
}