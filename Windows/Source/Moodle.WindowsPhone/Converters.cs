// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;

namespace PocketCampus.Moodle
{
    /// <summary>
    /// Convers a download state to a boolean indicating whether to display a progress indicator.
    /// </summary>
    public sealed class DownloadStateToBooleanConverter : ValueConverter<DownloadState, bool>
    {
        public override bool Convert( DownloadState value )
        {
            return value == DownloadState.Downloading;
        }
    }
}