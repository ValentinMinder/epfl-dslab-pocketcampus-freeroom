// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Moodle.Models;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;

namespace PocketCampus.Moodle
{
    /// <summary>
    /// Convers a download status to a boolean indicating whether to display a progress indicator.
    /// </summary>
    public sealed class DownloadStatusToBooleanConverter : ValueConverter<DownloadStatus, bool>
    {
        public override bool Convert( DownloadStatus value )
        {
            return value != DownloadStatus.None;
        }
    }

    /// <summary>
    /// Converts a file to an UIElement representing its icon.
    /// </summary>
    // TODO find a better way (also, for icons in general... maybe store them as strings and make a control)
    public sealed class FileToIconElementConverter : ValueConverter<MoodleFile, UIElement>
    {
        private static readonly Dictionary<string, string> Aliases = new Dictionary<string, string>
        {
            { "ppt", "pptx" },
            { "doc", "docx" },
            { "xls", "xlsx" }
        };
        private const int DefaultSize = 48;

        private readonly ResourceDictionary _resources = new ResourceDictionary
        {
            Source = new Uri( "ms-appx:///PocketCampus.Moodle.WindowsRuntime/Icons.xaml", UriKind.Absolute )
        };

        public override UIElement Convert( MoodleFile value )
        {
            string ext = value.Extension;
            if ( Aliases.ContainsKey( ext ) )
            {
                ext = Aliases[ext];
            }

            string resourceKey = "FileIcon_" + ext;
            if ( _resources.ContainsKey( resourceKey ) )
            {
                return new ContentControl
                {
                    Template = (ControlTemplate) _resources[resourceKey]
                };
            }

            if ( value.IconUrl != null )
            {
                return new Viewbox
                {
                    Stretch = Stretch.Uniform,
                    Child = new Image
                    {
                        Source = new BitmapImage( new Uri( value.IconUrl.Replace( "{size}", DefaultSize.ToString() ), UriKind.Absolute ) )
                    }
                };
            }

            return new ContentControl
            {
                Template = (ControlTemplate) _resources["DefaultFileIcon"]
            };
        }
    }
}