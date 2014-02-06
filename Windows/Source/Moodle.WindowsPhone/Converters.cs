// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.IO;
using System.Linq;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using PocketCampus.Common;
using PocketCampus.Moodle.Models;
using PocketCampus.Moodle.ViewModels;

namespace PocketCampus.Moodle
{
    /// <summary>
    /// Converts a file name to an image representing its extension.
    /// </summary>
    public sealed class FileNameToImageConverter : ValueConverter<string, ImageSource>
    {
        protected override ImageSource Convert( string value )
        {
            string ext = Path.GetExtension( value ).Substring( 1 ).ToLower();
            string uri = string.Format( "/Assets/Extension_{0}.png", ext );
            return new BitmapImage( new Uri( uri, UriKind.Relative ) );
        }
    }

    /// <summary>
    /// Removes the extension in a file name.
    /// </summary>
    public sealed class RemoveExtensionConverter : ValueConverter<string, string>
    {
        protected override string Convert( string value )
        {
            return Path.GetFileNameWithoutExtension( value );
        }
    }

    /// <summary>
    /// Convers a download state to a boolean indicating whether to display a progress indicator.
    /// </summary>
    public sealed class DownloadStateToBooleanConverter : ValueConverter<DownloadState, bool>
    {
        protected override bool Convert( DownloadState value )
        {
            return value == DownloadState.Downloading;
        }
    }

    /// <summary>
    /// Plumbing to display courses in LongListSelectors.
    /// </summary>
    public sealed class CourseSectionsToGroupsConverter : ValueConverter<CourseSection[], CourseSectionAsGroup[]>
    {
        protected override CourseSectionAsGroup[] Convert( CourseSection[] value )
        {
            return value.Select( section => new CourseSectionAsGroup( section ) ).ToArray();
        }
    }
}