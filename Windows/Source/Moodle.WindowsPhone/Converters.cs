// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using PocketCampus.Common;
using PocketCampus.Moodle.Models;

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

    /// <summary>
    /// Converts a file to an UIElement representing its icon.
    /// </summary>
    public sealed class FileToIconElementConverter : ValueConverter<MoodleFile, UIElement>
    {
        public override UIElement Convert( MoodleFile value )
        {
            string resourceKey = "FileIcon_" + value.Extension;
            if ( Application.Current.Resources.Contains( resourceKey ) )
            {
                return new ContentControl
                {
                    Template = (ControlTemplate) Application.Current.Resources[resourceKey]
                };
            }
            if ( value.IconUrl != null )
            {
                return new Viewbox
                {
                    Stretch = Stretch.Uniform,
                    Child = new Image
                    {
                        Source = new BitmapImage( new Uri( value.IconUrl.Replace( "{size}", "64" ), UriKind.Absolute ) )
                    }
                };
            }
            return new ContentControl
            {
                Template = (ControlTemplate) Application.Current.Resources["DefaultFileIcon"]
            };
        }
    }
}