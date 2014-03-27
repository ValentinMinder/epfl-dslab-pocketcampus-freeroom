// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.IO;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using PocketCampus.Common;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle
{
    /// <summary>
    /// Converts a file name to an icon template representing its extension.
    /// </summary>
    public sealed class FileNameToIconTemplateConverter : ValueConverter<string, ControlTemplate>
    {
        public override ControlTemplate Convert( string value )
        {
            string ext = Path.GetExtension( value ).Substring( 1 ).ToLower();
            string key = "FileIcon_" + ext;
            if ( Application.Current.Resources.Contains( key ) )
            {
                return (ControlTemplate) Application.Current.Resources[key];
            }
            return null;
        }
    }

    /// <summary>
    /// Removes the extension in a file name.
    /// </summary>
    public sealed class RemoveExtensionConverter : ValueConverter<string, string>
    {
        public override string Convert( string value )
        {
            return Path.GetFileNameWithoutExtension( value );
        }
    }

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

    /// <summary>
    /// Plumbing to display courses in LongListSelectors.
    /// </summary>
    public sealed class CourseSectionsToGroupsConverter : ValueConverter<CourseSection[], CourseSectionAsGroup[]>
    {
        public override CourseSectionAsGroup[] Convert( CourseSection[] value )
        {
            return value.Select( section => new CourseSectionAsGroup( section ) ).ToArray();
        }
    }
}